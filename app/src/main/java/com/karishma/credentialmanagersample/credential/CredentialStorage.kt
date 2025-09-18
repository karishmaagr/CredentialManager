package com.droidcon.credentialmanagersample.credential

import android.content.Context
import android.content.SharedPreferences
import com.droidcon.credentialmanagersample.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CredentialStorage(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("credential_storage", Context.MODE_PRIVATE)
    private val credentialsKey = "stored_credentials"
    private val biometricKey = "biometric_enabled"
    
    data class StoredCredential(
        val id: String,
        val email: String,
        val password: String,
        val name: String,
        val hasBiometric: Boolean = false
    )
    
    suspend fun saveCredentials(credential: StoredCredential): Boolean = withContext(Dispatchers.IO) {
        try {
            val existingCredentials = getStoredCredentials().toMutableList()
            
            // Remove existing credential with same email
            existingCredentials.removeAll { it.email == credential.email }
            
            // Add new credential
            existingCredentials.add(credential)
            
            // Save to SharedPreferences
            val jsonArray = JSONArray()
            existingCredentials.forEach { cred ->
                val jsonObject = JSONObject().apply {
                    put("id", cred.id)
                    put("email", cred.email)
                    put("password", cred.password)
                    put("name", cred.name)
                    put("hasBiometric", cred.hasBiometric)
                }
                jsonArray.put(jsonObject)
            }
            
            prefs.edit()
                .putString(credentialsKey, jsonArray.toString())
                .apply()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getStoredCredentials(): List<StoredCredential> = withContext(Dispatchers.IO) {
        try {
            val jsonString = prefs.getString(credentialsKey, "[]") ?: "[]"
            val jsonArray = JSONArray(jsonString)
            val credentials = mutableListOf<StoredCredential>()
            
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                credentials.add(
                    StoredCredential(
                        id = jsonObject.getString("id"),
                        email = jsonObject.getString("email"),
                        password = jsonObject.getString("password"),
                        name = jsonObject.getString("name"),
                        hasBiometric = jsonObject.optBoolean("hasBiometric", false)
                    )
                )
            }
            
            credentials
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun findCredentialByEmail(email: String): StoredCredential? = withContext(Dispatchers.IO) {
        getStoredCredentials().find { it.email == email }
    }
    
    suspend fun findCredentialByBiometric(): StoredCredential? = withContext(Dispatchers.IO) {
        getStoredCredentials().find { it.hasBiometric }
    }
    
    suspend fun enableBiometricForCredential(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val credentials = getStoredCredentials().toMutableList()
            val credentialIndex = credentials.indexOfFirst { it.email == email }
            
            if (credentialIndex != -1) {
                // Disable biometric for all other credentials
                credentials.forEachIndexed { index, cred ->
                    credentials[index] = cred.copy(hasBiometric = false)
                }
                
                // Enable biometric for this credential
                credentials[credentialIndex] = credentials[credentialIndex].copy(hasBiometric = true)
                
                // Save updated credentials
                val jsonArray = JSONArray()
                credentials.forEach { cred ->
                    val jsonObject = JSONObject().apply {
                        put("id", cred.id)
                        put("email", cred.email)
                        put("password", cred.password)
                        put("name", cred.name)
                        put("hasBiometric", cred.hasBiometric)
                    }
                    jsonArray.put(jsonObject)
                }
                
                prefs.edit()
                    .putString(credentialsKey, jsonArray.toString())
                    .putBoolean(biometricKey, true)
                    .apply()
                
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun disableBiometric(): Boolean = withContext(Dispatchers.IO) {
        try {
            val credentials = getStoredCredentials().toMutableList()
            val updatedCredentials = credentials.map { it.copy(hasBiometric = false) }
            
            val jsonArray = JSONArray()
            updatedCredentials.forEach { cred ->
                val jsonObject = JSONObject().apply {
                    put("id", cred.id)
                    put("email", cred.email)
                    put("password", cred.password)
                    put("name", cred.name)
                    put("hasBiometric", cred.hasBiometric)
                }
                jsonArray.put(jsonObject)
            }
            
            prefs.edit()
                .putString(credentialsKey, jsonArray.toString())
                .putBoolean(biometricKey, false)
                .apply()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun isBiometricEnabled(): Boolean = withContext(Dispatchers.IO) {
        prefs.getBoolean(biometricKey, false)
    }
    
    suspend fun deleteCredential(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val credentials = getStoredCredentials().toMutableList()
            val wasRemoved = credentials.removeAll { it.email == email }
            
            if (wasRemoved) {
                val jsonArray = JSONArray()
                credentials.forEach { cred ->
                    val jsonObject = JSONObject().apply {
                        put("id", cred.id)
                        put("email", cred.email)
                        put("password", cred.password)
                        put("name", cred.name)
                        put("hasBiometric", cred.hasBiometric)
                    }
                    jsonArray.put(jsonObject)
                }
                
                prefs.edit()
                    .putString(credentialsKey, jsonArray.toString())
                    .apply()
            }
            
            wasRemoved
        } catch (e: Exception) {
            false
        }
    }
}
