package com.harissabil.zakatkuy.ui.screen.maps

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.harissabil.zakatkuy.core.location.LocationHelper
import com.harissabil.zakatkuy.core.location.LocationTracker
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.maps.MapsRepository
import com.harissabil.zakatkuy.data.maps.dto.ResultsItem
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
class MapsViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    private val locationTracker: LocationTracker,
    private val mapsRepository: MapsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MapsState())
    val state: StateFlow<MapsState> = _state.asStateFlow()

    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow: SharedFlow<UIEvent> = _eventFlow.asSharedFlow()

    var isNearbyMasjidSearched = false

    init {
        updateLocationServiceStatus()
        if (_isLocationEnabled.value) {
            requestLocationUpdate()
            getNearbyMasjid()
        }
    }

    fun getNearbyMasjid() = viewModelScope.launch {
        if (_state.value.currentLocation == null) {
            Timber.e("Current location is null!")
            _eventFlow.emit(UIEvent.ShowSnackbar("Location not found!"))
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        when (val response = mapsRepository.getNearbyMosques(_state.value.currentLocation!!)) {
            is Resource.Success -> {
                Timber.d("Success get nearby masjid: ${response.data}")
                _state.update {
                    it.copy(
                        nearbyMasjid = response.data?.results ?: emptyList(),
                        isLoading = false
                    )
                }

                if (response.data?.results.isNullOrEmpty()) {
                    _eventFlow.emit(UIEvent.ShowSnackbar("Tidak ada masjid terdekat!"))
                }
            }

            is Resource.Error -> {
                _state.update { it.copy(isLoading = false) }
                Timber.e("Error: ${response.message}")
                _eventFlow.emit(
                    UIEvent.ShowSnackbar(
                        response.message ?: "Gagal mendapatkan data masjid terdekat!"
                    )
                )
            }

            is Resource.Loading -> {}
        }
    }

    fun getDirection(selectedMasjid: ResultsItem, origin: LatLng, destination: LatLng) =
        viewModelScope.launch {
            _state.update {
                it.copy(
                    routePoints = null,
                    selectedMasjid = selectedMasjid
                )
            }

            when (val pathResult = mapsRepository.getDirections(origin, destination)) {
                is Resource.Success -> {
                    _state.update { it.copy(routePoints = pathResult.data) }
                    getMasjidDetail(selectedMasjid)
                }

                is Resource.Error -> {
                    _eventFlow.emit(
                        UIEvent.ShowSnackbar(
                            pathResult.message ?: "Gagal mendapatkan rute!"
                        )
                    )
                }

                is Resource.Loading -> {}
            }
        }

    private fun getMasjidDetail(selectedMasjid: ResultsItem) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val response = mapsRepository.getPlaceDetails(selectedMasjid.placeId!!)) {
            is Resource.Success -> {
                _state.update { it.copy(selectedMasjidDetail = response.data, isLoading = false) }
            }

            is Resource.Error -> {
                _state.update { it.copy(isLoading = false) }
                Timber.e("Error: ${response.message}")
                _eventFlow.emit(
                    UIEvent.ShowSnackbar(
                        response.message ?: "Gagal mendapatkan detail masjid!"
                    )
                )
            }

            is Resource.Loading -> {}
        }
    }

    fun requestLocationUpdate() = viewModelScope.launch {
        locationTracker.requestLocationUpdates().collect { location ->
            _state.update { it.copy(currentLocation = location) }

            if (!isNearbyMasjidSearched) {
                getNearbyMasjid()
                isNearbyMasjidSearched = true
            }
        }
    }

    private fun updateLocationServiceStatus() {
        _isLocationEnabled.value = locationHelper.isConnected()
    }

    fun resetSelectedMasjid() {
        _state.update {
            it.copy(
                selectedMasjid = null,
                selectedMasjidDetail = null,
            )
        }
    }

    fun enableLocationRequest(
        context: Context,
        makeRequest: (intentSenderRequest: IntentSenderRequest) -> Unit,//Lambda to call when locations are off.
    ) {
        val locationRequest = LocationRequest.Builder( //Create a location request object
            Priority.PRIORITY_HIGH_ACCURACY, //Self explanatory
            10000 //Interval -> shorter the interval more frequent location updates
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build()) //Checksettings with building a request
        task.addOnSuccessListener { locationSettingsResponse ->
            Timber.tag("Location")
                .d("enableLocationRequest: LocationService Already Enabled $locationSettingsResponse")
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution)
                            .build() //Create the request prompt
                    makeRequest(intentSenderRequest) //Make the request from UI
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}