package com.droidcon.credentialmanagersample.passkey

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.droidcon.credentialmanagersample.credential.CredentialStorage
import com.droidcon.credentialmanagersample.data.AuthResult
import com.droidcon.credentialmanagersample.data.User
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.resume

class PasskeyService(private val context: Context) {
    
    private val biometricManager = BiometricManager.from(context)
    private val credentialStorage = CredentialStorage(context)
    
    fun isBiometricAvailable(): Boolean {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }
    
    fun getBiometricStatus(): BiometricStatus {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NONE_ENROLLED
            else -> BiometricStatus.UNKNOWN_ERROR
        }
    }
    
    suspend fun authenticateWithBiometric(
        activity: FragmentActivity,
        title: String = "Biometric Authentication",
        subtitle: String = "Use your biometric to authenticate",
        description: String = "Place your finger on the sensor or look at the camera"
    ): AuthResult = suspendCancellableCoroutine { continuation ->
        
        // First check if there's a credential with biometric enabled
        CoroutineScope(Dispatchers.IO).launch {
            val biometricCredential = credentialStorage.findCredentialByBiometric()
            if (biometricCredential == null) {
                continuation.resume(AuthResult(isSuccess = false, errorMessage = "No biometric credentials found. Please set up a passkey first."))
                return@launch
            }
            
            // Switch to main thread for BiometricPrompt
            CoroutineScope(Dispatchers.Main).launch {
                val executor = ContextCompat.getMainExecutor(context)
                val callback = createBiometricCallback(continuation, biometricCredential)
                val biometricPrompt = BiometricPrompt(activity, executor, callback)
                
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setNegativeButtonText("Cancel")
                    .build()
                
                continuation.invokeOnCancellation {
                    biometricPrompt.cancelAuthentication()
                }
                
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }
    
    private fun createBiometricCallback(
        continuation: kotlinx.coroutines.CancellableContinuation<AuthResult>,
        biometricCredential: CredentialStorage.StoredCredential
    ): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Return the actual stored user credentials
                val user = User(
                    id = biometricCredential.id,
                    email = biometricCredential.email,
                    name = biometricCredential.name
                )
                continuation.resume(AuthResult(isSuccess = true, user = user))
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                continuation.resume(AuthResult(isSuccess = false, errorMessage = errString.toString()))
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                continuation.resume(AuthResult(isSuccess = false, errorMessage = "Authentication failed"))
            }
        }
    }
    
    suspend fun createPasskey(
        activity: FragmentActivity,
        email: String,
        name: String,
        password: String
    ): AuthResult = suspendCancellableCoroutine { continuation ->
        
        // Ensure BiometricPrompt is called on main thread
        CoroutineScope(Dispatchers.Main).launch {
            val executor = ContextCompat.getMainExecutor(context)
            val callback = createPasskeyCallback(continuation, email, name, password)
            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Create Passkey")
                .setSubtitle("Set up biometric authentication for $email")
                .setDescription("Use your fingerprint, face, or device PIN to create a secure passkey")
                .setNegativeButtonText("Cancel")
                .build()
            
            continuation.invokeOnCancellation {
                biometricPrompt.cancelAuthentication()
            }
            
            biometricPrompt.authenticate(promptInfo)
        }
    }
    
    private fun createPasskeyCallback(
        continuation: kotlinx.coroutines.CancellableContinuation<AuthResult>,
        email: String,
        name: String,
        password: String
    ): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                
                // Create credential with biometric enabled
                val credential = CredentialStorage.StoredCredential(
                    id = "user_${System.currentTimeMillis()}",
                    email = email,
                    password = password,
                    name = name,
                    hasBiometric = true
                )
                
                // Save credential and enable biometric
                CoroutineScope(Dispatchers.IO).launch {
                    val success = credentialStorage.saveCredentials(credential) && 
                                 credentialStorage.enableBiometricForCredential(email)
                    
                    if (success) {
                        val user = User(
                            id = credential.id,
                            email = credential.email,
                            name = credential.name
                        )
                        continuation.resume(AuthResult(isSuccess = true, user = user))
                    } else {
                        continuation.resume(AuthResult(isSuccess = false, errorMessage = "Failed to save passkey"))
                    }
                }
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                continuation.resume(AuthResult(isSuccess = false, errorMessage = errString.toString()))
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                continuation.resume(AuthResult(isSuccess = false, errorMessage = "Passkey creation failed"))
            }
        }
    }
    
    suspend fun hasBiometricCredentials(): Boolean {
        return credentialStorage.findCredentialByBiometric() != null
    }
    
    fun hasBiometricCredentialsSync(): Boolean {
        return try {
            // This is a synchronous check for the ViewModel
            // In a real app, you might want to cache this value
            false // We'll implement this properly with coroutines
        } catch (e: Exception) {
            false
        }
    }
}

enum class BiometricStatus {
    AVAILABLE,
    NO_HARDWARE,
    HARDWARE_UNAVAILABLE,
    NONE_ENROLLED,
    UNKNOWN_ERROR
}
