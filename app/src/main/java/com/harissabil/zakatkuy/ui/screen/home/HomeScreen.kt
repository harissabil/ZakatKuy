package com.harissabil.zakatkuy.ui.screen.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.harissabil.zakatkuy.data.firestore.models.ChatHistory
import com.harissabil.zakatkuy.ui.components.FullscreenLoading
import com.harissabil.zakatkuy.ui.screen.home.components.ChatHistoryList
import com.harissabil.zakatkuy.ui.screen.home.components.PayZakatSection
import com.harissabil.zakatkuy.ui.screen.home.components.ProfileSection
import com.harissabil.zakatkuy.ui.screen.home.components.SwitchToAmilSheet
import com.harissabil.zakatkuy.ui.screen.home.components.ZakatHistoryCard
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToFormScreen: () -> Unit,
    onNavigateToChatScreen: (chatHistoryId: String?) -> Unit,
    onNavigateToMapsScreen: () -> Unit,
    onNavigateToHistoryScreen: () -> Unit,
    onNavigateToAmilHomeScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToPaymentScreen: (url: String) -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = state.isUserDataFetched) {
        Timber.i("isDataAvailable LE: ${state.isDataAvailable}, isUserDataFetched: ${state.isUserDataFetched}")
        if (!state.isDataAvailable && state.isUserDataFetched) {
            onNavigateToFormScreen()
        }
    }

    var isSwitchModeSheetVisible by rememberSaveable { mutableStateOf(false) }
    val switchModeSheetState = rememberModalBottomSheetState()

    val locationRequestLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                // User has enabled location
            } else {
                // User has not enabled location
                Timber.d("User has not enabled location")
            }
        }

    val requestMultiplePermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            // All permissions are granted
            if (!state.isLocationEnabled) {
                viewModel.enableLocationRequest(context = context) {
                    locationRequestLauncher.launch(it)
                }
            }
        } else {
            // Some permissions are denied
            Timber.d("Some permissions are denied")
        }
    }

    fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(key1 = state.requestLinkDto) {
        if (state.requestLinkDto != null) {
            onNavigateToPaymentScreen(state.requestLinkDto!!.paymentUrl).also {
                viewModel.resetPaymentLink()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        HomeContent(
            modifier = modifier.padding(innerPadding),
            state = state,
            lazyListState = lazyListState,
            onDropDownClick = {
                isSwitchModeSheetVisible = true
            },
            onHistoryClick = onNavigateToHistoryScreen,
            onPayZakatMalClick = { viewModel.createDynamicPaymentLink(context) },
            onPayZakatFitrahClick = {
                if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    requestMultiplePermissions.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        )
                    )
                } else {
                    onNavigateToMapsScreen()
                }
            },
            onChatHistoryClick = { chatHistory ->
                onNavigateToChatScreen(chatHistory.id)
            },
            onChatWithZakiClick = { onNavigateToChatScreen(null) }
        )

        if (isSwitchModeSheetVisible) {
            SwitchToAmilSheet(
                sheetState = switchModeSheetState,
                onSwitchToAmil = {
                    isSwitchModeSheetVisible = false
                    onNavigateToAmilHomeScreen()
                },
                onLogout = {
                    isSwitchModeSheetVisible = false
                    viewModel.signOut()
                    onNavigateToLoginScreen()
                },
                onDismissRequest = { isSwitchModeSheetVisible = false }
            )
        }
    }
    if (state.isLoading) {
        FullscreenLoading()
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeState,
    lazyListState: LazyListState,
    onDropDownClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onPayZakatMalClick: () -> Unit,
    onPayZakatFitrahClick: () -> Unit,
    onChatHistoryClick: (ChatHistory) -> Unit,
    onChatWithZakiClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileSection(
            avatarUrl = state.avatarUrl,
            name = state.name,
            onDropDownClick = onDropDownClick
        )
        ZakatHistoryCard(
            zakatTotal = state.totalZakatMal,
            onHistoryClick = onHistoryClick
        )
        PayZakatSection(
            onPayZakatMalClick = onPayZakatMalClick,
            onPayZakatFitrahClick = onPayZakatFitrahClick
        )
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = "Riwayat Zakat",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        ChatHistoryList(
            modifier = Modifier.weight(1f),
            lazyListState = lazyListState,
            chatHistoryList = state.chatHistoryList,
            onChatHistoryClick = onChatHistoryClick
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onChatWithZakiClick
        ) {
            Text(text = "Chat Zaki")
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview() {
    ZakatKuyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            HomeContent(
                state = HomeState(
                    name = "John Doe",
                    avatarUrl = null,
                    chatHistoryList = listOf(
                        ChatHistory(
                            id = "1",
                            timestamp = Timestamp.now()
                        ),
                        ChatHistory(
                            id = "2",
                            timestamp = Timestamp.now()
                        ),
                        ChatHistory(
                            id = "3",
                            timestamp = Timestamp.now()
                        )
                    )
                ),
                lazyListState = rememberLazyListState(),
                onDropDownClick = {},
                onHistoryClick = {},
                onPayZakatMalClick = {},
                onPayZakatFitrahClick = {},
                onChatHistoryClick = {},
                onChatWithZakiClick = {}
            )
        }
    }
}