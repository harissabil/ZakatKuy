package com.harissabil.zakatkuy.ui.screen.payment

import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.harissabil.zakatkuy.ui.components.WebBrowser

@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    url: String,
    onNavigateUp: () -> Unit,
) {
    Scaffold { innerPadding ->
        WebBrowser(
            modifier = modifier.padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            ),
            url = url,
            title = "Pembayaran",
            onNavigateBack = onNavigateUp
        )
    }
}