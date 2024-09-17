package com.harissabil.zakatkuy.ui.screen.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isInSignInProcess: Boolean = false,
    val isSuccessful: Boolean = false,
    val isDataAvailable: Boolean = false,
)
