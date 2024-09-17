package com.harissabil.zakatkuy.ui.screen.amil_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybercute.share.data.auth.AuthRepository
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.firestore.FirestoreRepository
import com.harissabil.zakatkuy.data.gemini.GeminiClient
import com.harissabil.zakatkuy.ui.screen.home.HomeViewModel.UIEvent
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
class AmilHomeViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository,
    private val geminiClient: GeminiClient,
) : ViewModel() {

    private val _state = MutableStateFlow(AmilHomeState())
    val state: StateFlow<AmilHomeState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow: SharedFlow<UIEvent> = _eventFlow.asSharedFlow()

    init {
        getSignedInUser()
        getZakatDocumentationList()
    }

    fun getZakatDocumentationList() = viewModelScope.launch {
        firestoreRepository.getZakatDocumentation().collect { response ->
            when (response) {
                is Resource.Success -> {
                    _state.update { it.copy(zakatDocumentationList = response.data ?: emptyList()) }
                }

                is Resource.Loading -> {}
                is Resource.Error -> {
                    Timber.e(response.message)
                    _eventFlow.emit(
                        UIEvent.ShowSnackbar(
                            response.message ?: "Something went wrong!"
                        )
                    )
                }
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        authRepository.signOut().data?.let {
            _eventFlow.emit(UIEvent.ShowSnackbar("Signed out successfully!"))
        }
    }

    fun onSpeechToTextResult(result: String) {
        _state.update { it.copy(speechToTextResult = result) }
    }

    private fun getSignedInUser() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        when (val response = authRepository.getSignedInUser()) {
            is Resource.Error -> {
                _eventFlow.tryEmit(
                    UIEvent.ShowSnackbar(
                        response.message ?: "Something went wrong!"
                    )
                )
                _state.update { it.copy(isLoading = false) }
            }

            is Resource.Loading -> {}
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        name = response.data?.userName ?: response.data?.email,
                        avatarUrl = response.data?.profilePictureUrl,
                        isLoading = false,
                    )
                }
            }
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}