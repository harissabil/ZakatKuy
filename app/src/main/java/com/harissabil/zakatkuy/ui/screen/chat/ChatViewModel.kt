package com.harissabil.zakatkuy.ui.screen.chat

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybercute.share.data.auth.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.firestore.FirestoreRepository
import com.harissabil.zakatkuy.data.firestore.models.ChatHistory
import com.harissabil.zakatkuy.data.firestore.models.ChatMessage
import com.harissabil.zakatkuy.data.firestore.models.ZakatMalHistory
import com.harissabil.zakatkuy.data.firestore.models.toContent
import com.harissabil.zakatkuy.data.gemini.GeminiClient
import com.harissabil.zakatkuy.data.midtrans.MidtransRepository
import com.harissabil.zakatkuy.data.midtrans.models.CreditCard
import com.harissabil.zakatkuy.data.midtrans.models.CustomerDetails
import com.harissabil.zakatkuy.data.midtrans.models.Expiry
import com.harissabil.zakatkuy.data.midtrans.models.ItemDetails
import com.harissabil.zakatkuy.data.midtrans.models.PaymentLinkRequest
import com.harissabil.zakatkuy.data.midtrans.models.TransactionDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository,
    private val geminiClient: GeminiClient,
    private val midtransRepository: MidtransRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val chat = geminiClient.geneminiChatFlashModel.startChat(
        history = _state.value.chatMessages.map { it.toContent() }
    )

    fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun sendPrompt(prompt: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                isWaitingResponse = true,
                query = "",
                chatMessages = it.chatMessages.toMutableList().apply {
                    add(
                        0,
                        ChatMessage(
                            id = UUID.randomUUID().toString(),
                            message = prompt,
                            isUser = true,
                            order = it.chatMessages.size
                        )
                    )
                }
            )
        }

        try {
            val response = chat.sendMessage(prompt)

            val gson = Gson()
            val message = gson.fromJson(response.text, ChatMessage::class.java)

            Timber.i("response: $message")

            _state.update {
                it.copy(
                    isWaitingResponse = false,
                    chatMessages = it.chatMessages.toMutableList().apply {
                        add(
                            0,
                            ChatMessage(
                                id = UUID.randomUUID().toString(),
                                message = message.message,
                                isUser = false,
                                zakatCategory = message.zakatCategory,
                                order = it.chatMessages.size,
                                zakatMalToPay = message.zakatMalToPay,
                                isGoingToPayZakatFitrah = message.isGoingToPayZakatFitrah,
                            )
                        )
                    }
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isWaitingResponse = false,
                    chatMessages = it.chatMessages.toMutableList().apply {
                        add(
                            0,
                            ChatMessage(
                                id = UUID.randomUUID().toString(),
                                message = "Afwan, ada kesalahan dalam sistem kami. Mohon coba lagi nanti.",
                                isUser = false,
                                order = it.chatMessages.size
                            )
                        )
                    }
                )
            }
        }
    }

    fun saveChatHistory(context: Context) = viewModelScope.launch {
        if (_state.value.chatMessages.size <= 1) return@launch

        _state.update { it.copy(isLoading = true) }

        val chatHistory = ChatHistory(
            id = UUID.randomUUID().toString(),
            email = authRepository.getSignedInUser().data?.email,
            timestamp = Timestamp.now()
        )

        val response = firestoreRepository.addChatHistoryAndMessages(
            chatHistory = chatHistory,
            messages = _state.value.chatMessages
        )

        when (response) {
            is Resource.Error -> {
                Timber.e("onSaveChatHistory: ${response.message}")
                _state.update { it.copy(isSavingChatProcessed = true, isLoading = false) }
                Toast.makeText(
                    context,
                    "Gagal menyimpan riwayat chat: ${response.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is Resource.Loading -> {}
            is Resource.Success -> {
                Timber.i("onSaveChatHistory: Success")
                _state.update { it.copy(isSavingChatProcessed = true, isLoading = false) }
                Toast.makeText(context, "Berhasil menyimpan riwayat chat!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun getChatHistory(chatHistoryId: String) = viewModelScope.launch {
        when (val response = firestoreRepository.getChatMessages(chatHistoryId)) {
            is Resource.Error -> {
                Timber.e("onGetChatHistory: ${response.message}")
            }

            is Resource.Loading -> {}
            is Resource.Success -> {
                _state.update {
                    it.copy(chatMessages = response.data ?: emptyList())
                }
            }
        }
    }

    fun createPaymentLink(amount: Int, category: String, context: Context) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val randomId = UUID.randomUUID().toString()

        // must be less than 36 characters
        val randomOrderId = UUID.randomUUID().toString().substring(0, 10)

        val response = midtransRepository.createPaymentLink(
            paymentLinkRequest = PaymentLinkRequest(
                transaction_details = TransactionDetails(
                    order_id = "zakatkuy-${randomOrderId}",
                    gross_amount = amount,
                    payment_link_id = "zakatkuy-id-${randomId}"
                ),
                credit_card = CreditCard(secure = true),
                usage_limit = 1,
                expiry = Expiry(
                    duration = 1,
                    unit = "days"
                ),
                item_details = listOf(
                    ItemDetails(
                        id = "zakat-$category",
                        name = "Zakat $category",
                        price = amount,
                        quantity = 1
                    )
                ),
                customer_details = CustomerDetails(
                    first_name = Firebase.auth.currentUser?.displayName
                        ?: Firebase.auth.currentUser?.email ?: "",
                    last_name = "Fulan",
                    email = Firebase.auth.currentUser?.email ?: "",
                    phone = "0812345678",
                    notes = ""
                )
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
                val addToFirestore = firestoreRepository.addZakatMalHistory(
                    zakatMalHistory = ZakatMalHistory(
                        id = randomId,
                        midtransId = response.data?.orderId,
                        email = Firebase.auth.currentUser?.email,
                        category = category,
                        amount = amount.toLong(),
                        timestamp = Timestamp.now(),
                        status = "pending"
                    )
                )

                when (addToFirestore) {
                    is Resource.Error -> {
                        Timber.e("onCreatePaymentLink: ${addToFirestore.message}")
                        _state.update { it.copy(isLoading = false) }
                        Toast.makeText(
                            context,
                            "Gagal membuat link pembayaran: ${addToFirestore.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                requestLinkDto = response.data,
                                isLoading = false
                            )
                        }
                    }
                }
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
}