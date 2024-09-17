package com.harissabil.zakatkuy.ui.screen.dashboard_amil

import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.harissabil.zakatkuy.ui.components.WebBrowser

@Composable
fun DashboardAmilScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
) {
    val url by rememberSaveable { mutableStateOf("www.google.com") } // TODO: Change this to the actual URL

    Scaffold { innerPadding ->
        WebBrowser(
            modifier = modifier.padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            ),
            url = url,
            title = "Dashboard Amil",
            onNavigateBack = onNavigateUp
        )
    }
}