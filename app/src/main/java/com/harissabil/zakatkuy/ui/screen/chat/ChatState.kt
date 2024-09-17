package com.harissabil.zakatkuy.ui.screen.chat

import com.harissabil.zakatkuy.data.firestore.models.ChatMessage
import com.harissabil.zakatkuy.data.midtrans.dto.RequestLinkDto
import java.util.UUID

data class ChatState(
    val query: String = "",
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            order = 0,
            message = "Assalamualaikum akhi! Ana Zaki, asisten virtual antum dalam membantu perhitungan zakat dan menjawab pertanyaan seputar zakat. Ana siap membantu menghitung zakat penghasilan, zakat emas, zakat perdagangan, zakat fitrah, zakat ternak, dan jenis zakat lainnya. Silakan tanyakan apa pun tentang zakat, insyaAllah ana siap membantu!",
        )
    ),
    val isWaitingResponse: Boolean = false,
    val isLoading: Boolean = false,
    val isSavingChatProcessed: Boolean = false,
    val requestLinkDto: RequestLinkDto? = null,
)
