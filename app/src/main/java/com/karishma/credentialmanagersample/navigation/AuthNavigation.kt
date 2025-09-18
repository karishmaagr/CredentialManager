package com.droidcon.credentialmanagersample.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.droidcon.credentialmanagersample.ui.screens.DashboardScreen
import com.droidcon.credentialmanagersample.ui.screens.SignInScreen
import com.droidcon.credentialmanagersample.ui.screens.SignUpScreen

@Composable
fun AuthNavigation(
    navController: NavHostController,
    onSignIn: (String, String) -> Unit,
    onSignInWithPasskey: () -> Unit,
    onSignUp: (String, String, String) -> Unit,
    onSignUpWithPasskey: (String, String, String) -> Unit,
    onSignOut: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    isAuthenticated: Boolean,
    user: com.droidcon.credentialmanagersample.data.User?,
    isPasskeyAvailable: Boolean = true
) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "dashboard" else "signin"
    ) {
        composable("signin") {
            SignInScreen(
                onSignIn = onSignIn,
                onSignInWithPasskey = onSignInWithPasskey,
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                isLoading = isLoading,
                errorMessage = errorMessage,
                isPasskeyAvailable = isPasskeyAvailable
            )
        }
        
        composable("signup") {
            SignUpScreen(
                onSignUp = onSignUp,
                onSignUpWithPasskey = onSignUpWithPasskey,
                onNavigateToSignIn = {
                    navController.navigate("signin")
                },
                isLoading = isLoading,
                errorMessage = errorMessage,
                isPasskeyAvailable = isPasskeyAvailable
            )
        }
        
        composable("dashboard") {
            if (user != null) {
                DashboardScreen(
                    user = user,
                    onSignOut = onSignOut,
                    isLoading = isLoading
                )
            }
        }
    }
}
