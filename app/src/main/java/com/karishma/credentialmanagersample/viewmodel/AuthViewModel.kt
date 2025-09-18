package com.droidcon.credentialmanagersample.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.credentialmanagersample.credential.CredentialManagerService
import com.droidcon.credentialmanagersample.data.AuthResult
import com.droidcon.credentialmanagersample.data.AuthState
import com.droidcon.credentialmanagersample.data.SignInRequest
import com.droidcon.credentialmanagersample.data.SignUpRequest
import com.droidcon.credentialmanagersample.data.User
import com.droidcon.credentialmanagersample.passkey.PasskeyService
import com.droidcon.credentialmanagersample.passkey.BiometricStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val credentialManagerService = CredentialManagerService(application)
    private val passkeyService = PasskeyService(application)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _biometricStatus = MutableStateFlow(BiometricStatus.UNKNOWN_ERROR)
    val biometricStatus: StateFlow<BiometricStatus> = _biometricStatus.asStateFlow()
    
    private val _showPasskeySetupDialog = MutableStateFlow(false)
    val showPasskeySetupDialog: StateFlow<Boolean> = _showPasskeySetupDialog.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        checkAuthState()
        checkBiometricStatus()
    }
    
    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = credentialManagerService.signInWithPassword(
                SignInRequest(email, password)
            )
            
            handleAuthResult(result)
        }
    }
    
    fun signUp(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }
        
        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = credentialManagerService.signUpWithPassword(
                SignUpRequest(email, password, name)
            )
            
            handleAuthResult(result)
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            credentialManagerService.signOut()
            _authState.value = AuthState.Unauthenticated
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun signInWithPasskey(activity: androidx.fragment.app.FragmentActivity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = passkeyService.authenticateWithBiometric(
                activity = activity,
                title = "Sign In with Passkey",
                subtitle = "Use your biometric to sign in",
                description = "Place your finger on the sensor, look at the camera, or use your device PIN"
            )
            
            handleAuthResult(result)
        }
    }
    
    fun signUpWithPasskey(activity: androidx.fragment.app.FragmentActivity, email: String, name: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = passkeyService.createPasskey(
                activity = activity,
                email = email,
                name = name,
                password = password
            )
            
            handleAuthResult(result)
        }
    }
    
    fun isPasskeyAvailable(): Boolean {
        return passkeyService.isBiometricAvailable()
    }
    
    fun showPasskeySetupDialog() {
        _showPasskeySetupDialog.value = true
    }
    
    fun hidePasskeySetupDialog() {
        _showPasskeySetupDialog.value = false
    }
    
    fun setupPasskeyForCurrentUser(activity: androidx.fragment.app.FragmentActivity) {
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Create a temporary password for the user (in real app, you'd get this from secure storage)
                val tempPassword = "temp_password_${System.currentTimeMillis()}"
                
                val result = passkeyService.createPasskey(
                    activity = activity,
                    email = user.email,
                    name = user.name,
                    password = tempPassword
                )
                
                if (result.isSuccess) {
                    _showPasskeySetupDialog.value = false
                    // Update the current user with the new credential
                    _currentUser.value = result.user
                } else {
                    _errorMessage.value = result.errorMessage ?: "Failed to set up Passkey"
                }
                
                _isLoading.value = false
            }
        }
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = credentialManagerService.getSavedCredentials()
            if (result.isSuccess && result.user != null) {
                _authState.value = AuthState.Authenticated(result.user!!)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
            
            _isLoading.value = false
        }
    }
    
    private fun checkBiometricStatus() {
        _biometricStatus.value = passkeyService.getBiometricStatus()
    }
    
    private fun handleAuthResult(result: AuthResult) {
        _isLoading.value = false
        
        if (result.isSuccess && result.user != null) {
            _authState.value = AuthState.Authenticated(result.user!!)
            _currentUser.value = result.user
            _errorMessage.value = null
            
            // Check if this is a traditional login (not Passkey) and show setup dialog
            if (isPasskeyAvailable() && !passkeyService.hasBiometricCredentialsSync()) {
                _showPasskeySetupDialog.value = true
            }
        } else {
            _authState.value = AuthState.Error(result.errorMessage ?: "Authentication failed")
            _errorMessage.value = result.errorMessage
        }
    }
}
