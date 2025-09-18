package com.droidcon.credentialmanagersample.data

data class User(
    val id: String,
    val email: String,
    val name: String,
    val profilePicture: String? = null
)

data class AuthResult(
    val isSuccess: Boolean,
    val user: User? = null,
    val errorMessage: String? = null
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String
)
