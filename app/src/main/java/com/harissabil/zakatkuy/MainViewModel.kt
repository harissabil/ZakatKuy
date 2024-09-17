package com.harissabil.zakatkuy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.harissabil.zakatkuy.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    var splashCondition by mutableStateOf(true)
        private set

    private val _startDestination = MutableStateFlow<Route>(Route.Splash)
    val startDestination: StateFlow<Route> = _startDestination.asStateFlow()

    init {
        getStartDestination()
    }

    private fun getStartDestination() = viewModelScope.launch {
        delay(700)
        val isUserSignedIn = Firebase.auth.currentUser != null

        _startDestination.value = if (isUserSignedIn) {
            Route.Home
        } else {
            Route.Login
        }

        splashCondition = false
    }
}