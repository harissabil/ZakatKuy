package com.harissabil.zakatkuy.ui.screen.history

import com.harissabil.zakatkuy.data.firestore.models.ZakatMalHistory

data class HistoryState(
    val zakatMalHistories: List<ZakatMalHistory> = emptyList(),
)
