package com.harissabil.zakatkuy.ui.screen.home

import com.harissabil.zakatkuy.data.firestore.models.ChatHistory
import com.harissabil.zakatkuy.data.midtrans.dto.RequestLinkDto

data class HomeState(
    val name: String? = null,
    val avatarUrl: String? = null,
    val totalZakatMal: Long? = null,
    val chatHistoryList: List<ChatHistory> = emptyList(),

    val isLoading: Boolean = false,
    val isLocationEnabled: Boolean = false,
    val isUserDataFetched: Boolean = false,
    val isDataAvailable: Boolean = false,

    val requestLinkDto: RequestLinkDto? = null,
)