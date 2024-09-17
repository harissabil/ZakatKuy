package com.harissabil.zakatkuy.ui.screen.home

import android.content.Context
import android.content.IntentSender
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybercute.share.data.auth.AuthRepository
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.firestore.FirestoreRepository
import com.harissabil.zakatkuy.data.midtrans.MidtransRepository
import com.harissabil.zakatkuy.data.midtrans.models.CreditCard
import com.harissabil.zakatkuy.data.midtrans.models.CustomerDetails
import com.harissabil.zakatkuy.data.midtrans.models.DynamicAmount
import com.harissabil.zakatkuy.data.midtrans.models.DynamicPaymentLinkRequest
import com.harissabil.zakatkuy.data.midtrans.models.Expiry
import com.harissabil.zakatkuy.data.midtrans.models.TransactionDetails
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository,
    private val midtransRepository: MidtransRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow: SharedFlow<UIEvent> = _eventFlow.asSharedFlow()

    init {
        getUserData()
        getChatHistories()
        getZakatHistories()
    }

    private fun getUserData() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val isDataAvailable = firestoreRepository.getUserData().data != null

        Timber.i("isDataAvailable: $isDataAvailable")

        _state.update {
            it.copy(isDataAvailable = isDataAvailable, isLoading = false, isUserDataFetched = true)
        }

        if (!isDataAvailable) {
            return@launch
        }

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
                        isDataAvailable = true,
                        isUserDataFetched = true
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

    private fun getChatHistories() = viewModelScope.launch {
        firestoreRepository.getChatHistory().collect { response ->
            when (response) {
                is Resource.Error -> {
                    Timber.e("Error: ${response.message}")
                    _eventFlow.emit(
                        UIEvent.ShowSnackbar(
                            response.message ?: "Something went wrong!"
                        )
                    )
                }

                is Resource.Loading -> {}
                is Resource.Success -> {
                    Timber.d("ChatHistories: ${response.data}")
                    _state.update { it.copy(chatHistoryList = response.data ?: emptyList()) }
                }
            }
        }
    }

    fun enableLocationRequest(
        context: Context,
        makeRequest: (intentSenderRequest: IntentSenderRequest) -> Unit, //Lambda to call when locations are off.
    ) {
        val locationRequest = LocationRequest.Builder(//Create a location request object
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,//Self explanatory
            10000//Interval -> shorter the interval more frequent location updates
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())//Checksettings with building a request
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
                            .build()//Create the request prompt
                    makeRequest(intentSenderRequest)//Make the request from UI
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun getZakatHistories() = viewModelScope.launch {
        firestoreRepository.getZakatMalHistory().collect { response ->
            when (response) {
                is Resource.Success -> {
                    _state.update { itState ->
                        itState.copy(
                            totalZakatMal = response.data?.sumOf { it.amount?.toInt()!! }?.toLong(),
                        )
                    }
                }

                is Resource.Loading -> {}
                is Resource.Error -> {
                    Timber.e("getZakatHistories: ${response.message}")
                    _eventFlow.emit(
                        UIEvent.ShowSnackbar(
                            response.message ?: "Something went wrong!"
                        )
                    )
                }
            }
        }
    }

    fun createDynamicPaymentLink(context: Context) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val randomId = UUID.randomUUID().toString()

        // must be less than 36 characters
        val randomOrderId = UUID.randomUUID().toString().substring(0, 10)

        val response = midtransRepository.createDynamicPaymentLink(
            dynamicPaymentLinkRequest = DynamicPaymentLinkRequest(
                transaction_details = TransactionDetails(
                    order_id = "zakatkuy-${randomOrderId}",
                    gross_amount = 0,
                    payment_link_id = "zakatkuy-id-${randomId}"
                ),
                credit_card = CreditCard(secure = true),
                usage_limit = 1,
                expiry = Expiry(
                    duration = 1,
                    unit = "days"
                ),
                customer_details = CustomerDetails(
                    first_name = Firebase.auth.currentUser?.displayName
                        ?: Firebase.auth.currentUser?.email ?: "",
                    last_name = "Fulan",
                    email = Firebase.auth.currentUser?.email ?: "",
                    phone = "0812345678",
                    notes = ""
                ),
                dynamic_amount = DynamicAmount(),
                payment_link_type = "DYNAMIC_AMOUNT"
            )
        )

        when (response) {
            is Resource.Error -> {
                Timber.e("onCreatePaymentLink: ${response.message}")
                _state.update { it.copy(isLoading = false) }
                Toast.makeText(
                    context,
                    "Gagal membuat link pembayaran: ${response.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is Resource.Loading -> {}
            is Resource.Success -> {
                _state.update { it.copy(requestLinkDto = response.data, isLoading = false) }
            }
        }

//        _state.update { it.copy(isLoading = false, requestLinkDto = RequestLinkDto(
//            orderId = "zakatkuy-${randomOrderId}",
//            paymentUrl = "https://sandbox.midtrans.com/payment/${randomOrderId}",
//        )) }
    }

    fun resetPaymentLink() {
        _state.update { it.copy(requestLinkDto = null) }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}