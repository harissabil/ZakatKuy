package com.harissabil.zakatkuy.ui.screen.catetin

import com.harissabil.zakatkuy.data.firestore.models.ZakatDocumentation

data class CatetinState(
    val zakatDocumentation: ZakatDocumentation? = null,
    val isLoading: Boolean = false,
    val speechToTextResult: String? = null,

    val isSubmitSuccess: Boolean = false,
)
