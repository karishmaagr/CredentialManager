package com.droidcon.credentialmanagersample.credential

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.droidcon.credentialmanagersample.data.AuthResult
import com.droidcon.credentialmanagersample.data.SignInRequest
import com.droidcon.credentialmanagersample.data.SignUpRequest
import com.droidcon.credentialmanagersample.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CredentialManagerService(private val context: Context) {
    
    private val credentialManager = CredentialManager.create(context)
    private val credentialStorage = CredentialStorage(context)
    
    suspend fun signInWithPassword(request: SignInRequest): AuthResult = withContext(Dispatchers.IO) {
        try {
            // Check stored credentials
            val storedCredential = credentialStorage.findCredentialByEmail(request.email)
            
            if (storedCredential != null && storedCredential.password == request.password) {
                val user = User(
                    id = storedCredential.id,
                    email = storedCredential.email,
                    name = storedCredential.name
                )
                AuthResult(isSuccess = true, user = user)
            } else {
                AuthResult(isSuccess = false, errorMessage = "Invalid credentials")
            }
        } catch (e: Exception) {
            AuthResult(isSuccess = false, errorMessage = e.message ?: "Authentication failed")
        }
    }
    
    suspend fun signUpWithPassword(request: SignUpRequest): AuthResult = withContext(Dispatchers.IO) {
        try {
            // Check if user already exists
            val existingCredential = credentialStorage.findCredentialByEmail(request.email)
            if (existingCredential != null) {
                return@withContext AuthResult(isSuccess = false, errorMessage = "User already exists")
            }
            
            // Create new credential
            val credential = CredentialStorage.StoredCredential(
                id = "user_${System.currentTimeMillis()}",
                email = request.email,
                password = request.password,
                name = request.name,
                hasBiometric = false
            )
            
            val success = credentialStorage.saveCredentials(credential)
            
            if (success) {
                val user = User(
                    id = credential.id,
                    email = credential.email,
                    name = credential.name
                )
                AuthResult(isSuccess = true, user = user)
            } else {
                AuthResult(isSuccess = false, errorMessage = "Failed to create account")
            }
        } catch (e: Exception) {
            AuthResult(isSuccess = false, errorMessage = e.message ?: "Registration failed")
        }
    }
    
    suspend fun getSavedCredentials(): AuthResult = withContext(Dispatchers.IO) {
        try {
            val credentials = credentialStorage.getStoredCredentials()
            if (credentials.isNotEmpty()) {
                // Return the first credential (in a real app, you might want to show a list)
                val firstCredential = credentials.first()
                val user = User(
                    id = firstCredential.id,
                    email = firstCredential.email,
                    name = firstCredential.name
                )
                AuthResult(isSuccess = true, user = user)
            } else {
                AuthResult(isSuccess = false, errorMessage = "No saved credentials found")
            }
        } catch (e: Exception) {
            AuthResult(isSuccess = false, errorMessage = e.message ?: "Unknown error")
        }
    }
    
    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            // In a real app, you would clear credentials from Credential Manager
            // and sign out from your backend
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun isValidCredentials(email: String, password: String): Boolean {
        // Demo validation - in real app, validate against backend
        return email.isNotEmpty() && password.length >= 6
    }
    
    private fun extractNameFromEmail(email: String): String {
        return email.substringBefore("@").replace(".", " ").replace("_", " ")
            .split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
    
    private suspend fun saveCredentials(email: String, password: String) {
        // In a real app, you would save credentials to Credential Manager
        // This is a simplified version for demo purposes
    }
}
