package com.harissabil.zakatkuy.ui.screen.amil_home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harissabil.zakatkuy.ui.components.FullscreenLoading
import com.harissabil.zakatkuy.ui.screen.amil_home.components.CatetinHistoryList
import com.harissabil.zakatkuy.ui.screen.amil_home.components.SwitchToMuzakkiSheet
import com.harissabil.zakatkuy.ui.screen.home.components.ProfileSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmilHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AmilHomeViewModel = hiltViewModel(),
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToCatetinScreen: () -> Unit,
    onNavigateToDashboardAmilScreen: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val lazyListState = rememberLazyListState()

    val snackbarHostState = remember { SnackbarHostState() }

    var isSwitchModeSheetVisible by rememberSaveable { mutableStateOf(false) }
    val switchModeSheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(innerPadding)
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileSection(
                avatarUrl = state.avatarUrl,
                name = state.name,
                onDropDownClick = {
                    isSwitchModeSheetVisible = true
                }
            )
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = "Riwayat Pencatatan Zakat",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            CatetinHistoryList(
                lazyListState = lazyListState,
                zakatDocumentationList = state.zakatDocumentationList,
                onZakatDocumentationClick = {}
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
//                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id_ID") // Set bahasa Indonesia
//                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan berbicara...") // Pesan prompt untuk user
//                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L) // 10 detik setelah benar-benar diam
//                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L) // 5 detik untuk jeda
//                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000L) // Minimal 3 detik input aktif
//                }
//                launcher.launch(intent)
                    onNavigateToCatetinScreen()
                }) {
                Icon(
                    imageVector = Icons.Outlined.GraphicEq,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Catetin Zakat")
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
//                    val intent = Intent(context, DashboardAmilActivity::class.java)
//                    context.startActivity(intent)
                    onNavigateToDashboardAmilScreen()
                }) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Beranda si Amil")
            }
        }

        if (isSwitchModeSheetVisible) {
            SwitchToMuzakkiSheet(
                sheetState = switchModeSheetState,
                onSwitchToMuzakki = {
                    isSwitchModeSheetVisible = false
                    onNavigateToHomeScreen()
                },
                onLogout = {
                    viewModel.signOut()
                    isSwitchModeSheetVisible = false
                    onNavigateToLoginScreen()
                },
                onDismissRequest = {
                    isSwitchModeSheetVisible = false
                }
            )
        }
    }
    if (state.isLoading) {
        FullscreenLoading()
    }
}