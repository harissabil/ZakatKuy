package com.harissabil.zakatkuy.ui.screen.register

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import com.harissabil.zakatkuy.ui.components.AuthContent
import com.harissabil.zakatkuy.ui.components.FullscreenLoading
import com.harissabil.zakatkuy.utils.getGoogleIdToken
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToFormScreen: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegisterViewModel.UIEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = state.isSuccessful) {
        if (state.isSuccessful) {
            Timber.i("isDataAvailable: ${state.isDataAvailable}")
            if (state.isDataAvailable) {
                onNavigateToHomeScreen()
            } else {
                onNavigateToFormScreen()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        AuthContent(
            modifier = modifier.padding(innerPadding),
            title = "Ahlan Wa Sahlan!",
            subtitle = "Selamat datang! Silahkan mendaftar dengan memasukkan email dan password antum!",
            buttonTitle = "Register",
            bottomText = "Sudah punya akun?",
            clickableText = "Login",
            email = state.email,
            onEmailChange = viewModel::onEmailChanged,
            password = state.password,
            onPasswordChange = viewModel::onPasswordChanged,
            onRegisterClick = {
                keyboardController?.hide()
                viewModel.onRegisterClick()
            },
            onContinueWithGoogleClick = {
                scope.launch {
                    getGoogleIdToken(context)?.let {
                        viewModel.onContinueWithGoogle(it)
                    }
                }
            },
            onSignInClick = onNavigateToLoginScreen
        )
    }
    if (state.isLoading) {
        FullscreenLoading()
    }
}