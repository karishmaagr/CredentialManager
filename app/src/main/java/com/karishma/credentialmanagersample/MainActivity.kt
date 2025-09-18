package com.droidcon.credentialmanagersample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.droidcon.credentialmanagersample.data.AuthState
import com.droidcon.credentialmanagersample.navigation.AuthNavigation
import com.droidcon.credentialmanagersample.ui.components.PasskeySetupDialog
import com.droidcon.credentialmanagersample.ui.theme.CredentialManagerSampleTheme
import com.droidcon.credentialmanagersample.viewmodel.AuthViewModel
import com.droidcon.credentialmanagersample.passkey.BiometricStatus

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CredentialManagerSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CredentialManagerApp(this)
                }
            }
        }
    }
}

@Composable
fun CredentialManagerApp(activity: FragmentActivity) {
    val authViewModel: AuthViewModel = viewModel()
    val navController = rememberNavController()
    
    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val biometricStatus by authViewModel.biometricStatus.collectAsState()
    val showPasskeySetupDialog by authViewModel.showPasskeySetupDialog.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Clear error when navigating
    LaunchedEffect(navController.currentBackStackEntry) {
        authViewModel.clearError()
    }
    
    val isPasskeyAvailable = biometricStatus == BiometricStatus.AVAILABLE
    
    AuthNavigation(
        navController = navController,
        onSignIn = { email, password ->
            authViewModel.signIn(email, password)
        },
        onSignInWithPasskey = {
            authViewModel.signInWithPasskey(activity)
        },
        onSignUp = { name, email, password ->
            authViewModel.signUp(name, email, password)
        },
        onSignUpWithPasskey = { name, email, password ->
            authViewModel.signUpWithPasskey(activity, email, name, password)
        },
        onSignOut = {
            authViewModel.signOut()
        },
        isLoading = isLoading,
        errorMessage = errorMessage,
        isAuthenticated = authState is AuthState.Authenticated,
        user = (authState as? AuthState.Authenticated)?.user,
        isPasskeyAvailable = isPasskeyAvailable
    )
    
    // Passkey Setup Dialog
    PasskeySetupDialog(
        isVisible = showPasskeySetupDialog,
        onDismiss = { authViewModel.hidePasskeySetupDialog() },
        onSetupPasskey = { authViewModel.setupPasskeyForCurrentUser(activity) },
        onSkip = { authViewModel.hidePasskeySetupDialog() },
        userEmail = currentUser?.email ?: ""
    )
}