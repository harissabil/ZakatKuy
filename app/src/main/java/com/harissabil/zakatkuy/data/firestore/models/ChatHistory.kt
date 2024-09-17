package com.harissabil.zakatkuy.data.firestore.models

import androidx.annotation.Keep
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

@Keep
data class ChatHistory(
    @DocumentId
    val id: String? = null,

    val email: String? = null,

    val timestamp: Timestamp? = null,
)

data class ChatMessage(
    @DocumentId
    val id: String? = null,

    @get:PropertyName("chat_history_id")
    @set:PropertyName("chat_history_id")
    var chatHistoryId: String? = null,

    @get:PropertyName("is_user")
    @set:PropertyName("is_user")
    var isUser: Boolean? = null,
    val order: Int? = null,
    val message: String? = null,

    @get:PropertyName("zakat_category")
    @set:PropertyName("zakat_category")
    var zakatCategory: String? = null,

    @get:PropertyName("zakat_mal_to_pay")
    @set:PropertyName("zakat_mal_to_pay")
    var zakatMalToPay: Long? = null,

    @get:PropertyName("is_going_to_pay_zakat_fitrah")
    @set:PropertyName("is_going_to_pay_zakat_fitrah")
    var isGoingToPayZakatFitrah: Boolean? = null,
)

fun ChatMessage.toContent(): Content {
    return content(
        role = if (isUser == true) "user" else "model",
    ) {
        text(message ?: "")
    }
}
