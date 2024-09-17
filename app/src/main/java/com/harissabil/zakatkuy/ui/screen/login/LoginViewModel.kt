package com.harissabil.zakatkuy.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybercute.share.data.auth.AuthRepository
import com.cybercute.share.data.auth.model.SignedInResponse
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.firestore.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow: SharedFlow<UIEvent> = _eventFlow.asSharedFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    private fun onSignInResult(result: Resource<SignedInResponse>) {
        Timber.e("onSignInResult: ${result.data}")
        viewModelScope.launch {
            when (result) {
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isSuccessful = false,
                        isLoading = false,
                        isInSignInProcess = false
                    )
                    _eventFlow.emit(
                        UIEvent.ShowSnackbar(
                            result.message ?: "Something went wrong!"
                        )
                    )
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, isInSignInProcess = false)
                }

                is Resource.Success -> {
                    val isDataAvailable = firestoreRepository.getUserData()

                    when (isDataAvailable) {
                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                isSuccessful = true,
                                isLoading = false,
                                isInSignInProcess = false,
                                isDataAvailable = false
                            )
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _state.value = _state.value.copy(
                                isSuccessful = true,
                                isLoading = false,
                                isInSignInProcess = false,
                                isDataAvailable = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun onRegisterClick() {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {

            if (_state.value.email.isBlank() || _state.value.password.isBlank()) {
                _state.value = _state.value.copy(isLoading = false)
                _eventFlow.emit(UIEvent.ShowSnackbar("Please fill all the fields!"))
                return@launch
            }

            val registerResult = authRepository.registerWithEmail(
                _state.value.email,
                _state.value.password
            )
            onSignInResult(registerResult)
        }
    }

    fun onContinueWithGoogle(token: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            val signInResult = authRepository.signInWithGoogle(token)
            onSignInResult(signInResult)
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}