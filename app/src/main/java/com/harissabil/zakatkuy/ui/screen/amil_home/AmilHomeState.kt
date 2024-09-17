package com.harissabil.zakatkuy.ui.screen.amil_home

import com.harissabil.zakatkuy.data.firestore.models.ZakatDocumentation

data class AmilHomeState(
    val name: String? = "Amil",
    val avatarUrl: String? = null,
    val zakatDocumentationList: List<ZakatDocumentation> = emptyList(),

    val isLoading: Boolean = false,
    val speechToTextResult: String? = null,
)
