package com.harissabil.zakatkuy.ui.screen.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.speech.RecognizerIntent
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harissabil.zakatkuy.ui.components.FullscreenLoading
import com.harissabil.zakatkuy.ui.screen.chat.components.ChatList
import com.harissabil.zakatkuy.ui.screen.chat.components.ChatRecommendationChip
import com.harissabil.zakatkuy.ui.screen.chat.components.ChatTextField
import com.harissabil.zakatkuy.ui.screen.chat.components.ChatTopAppBar
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme
import kotlinx.coroutines.delay

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatHistoryId: String? = null,
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onNavigateToPaymentScreen: (url: String) -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState()

    val isKeyboardVisible by keyboardVisibility()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                viewModel.onQueryChanged(result?.get(0) + " ")
            } else {
                Toast.makeText(context, "Voice input failed", Toast.LENGTH_SHORT).show()
            }
        }

    LaunchedEffect(key1 = isKeyboardVisible) {
        if (isKeyboardVisible) {
            delay(250)
            lazyListState.animateScrollToItem(state.chatMessages.size - 1)
        }
    }

    LaunchedEffect(key1 = chatHistoryId) {
        if (chatHistoryId != null) {
            viewModel.getChatHistory(chatHistoryId)
        }
    }

    LaunchedEffect(key1 = state.isSavingChatProcessed) {
        if (state.isSavingChatProcessed) {
            delay(250)
            onNavigateUp()
        }
    }

    LaunchedEffect(key1 = state.requestLinkDto) {
        if (state.requestLinkDto != null) {
//            val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder()
//                .setUrlBarHidingEnabled(false)
//                .setShowTitle(true)
//                .build()
//            customTabsIntent.launchUrl(
//                context.applicationContext,
//                Uri.parse(state.requestLinkDto!!.paymentUrl)
//            )

//            val openUrl = Intent(Intent.ACTION_VIEW)
//            openUrl.data = Uri.parse(state.requestLinkDto!!.paymentUrl)
//            context.startActivity(openUrl)
            onNavigateToPaymentScreen(state.requestLinkDto!!.paymentUrl).also {
                viewModel.resetPaymentLink()
            }
        }
    }

    BackHandler {
        if (chatHistoryId != null) {
            onNavigateUp()
        } else
            handleBackPress(context, viewModel, state, onNavigateUp)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        topBar = {
            ChatTopAppBar(onNavigateUp = {
                if (chatHistoryId != null) {
                    onNavigateUp()
                } else
                    handleBackPress(
                        context,
                        viewModel,
                        state,
                        onNavigateUp
                    )
            })
        },
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
    ) { innerPadding ->
        ChatContent(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding(),
            chatHistoryId = chatHistoryId,
            state = state,
            lazyListState = lazyListState,
            onPayZakatMalClick = { amount, category ->
                if (category != null) {
                    viewModel.createPaymentLink(
                        amount.toInt(),
                        category,
                        context
                    )
                }
            },
            onPayZakatFitrahClick = { },
            onChipClick = { message ->
                viewModel.onQueryChanged(message)
            },
            onTextChange = viewModel::onQueryChanged,
            onSendClick = viewModel::sendPrompt,
            onVoiceClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id_ID") // Set bahasa Indonesia
                    putExtra(
                        RecognizerIntent.EXTRA_PROMPT,
                        "Silakan berbicara..."
                    ) // Pesan prompt untuk user
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                        10000L
                    ) // 10 detik setelah benar-benar diam
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                        10000L
                    ) // 5 detik untuk jeda
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                        10000L
                    ) // Minimal 3 detik input aktif
                }
                launcher.launch(intent)
            }
        )
    }
    if (state.isLoading) {
        FullscreenLoading()
    }
}

@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    chatHistoryId: String? = null,
    state: ChatState,
    lazyListState: LazyListState,
    onPayZakatMalClick: (amount: Long, category: String?) -> Unit,
    onPayZakatFitrahClick: () -> Unit,
    onChipClick: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onVoiceClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChatList(
            lazyListState = lazyListState,
            chatList = state.chatMessages,
            onPayZakatMalClick = onPayZakatMalClick,
            onPayZakatFitrahClick = onPayZakatFitrahClick
        )
        if (chatHistoryId != null) {
            return@Column
        }
        AnimatedVisibility(visible = state.chatMessages.size <= 2) {
            ChatRecommendationChip(
                onChipClick = onChipClick
            )
        }
        ChatTextField(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 24.dp),
            textValue = state.query,
            onTextChange = onTextChange,
            onSendClick = onSendClick,
            onVoiceClick = onVoiceClick,
            isWaitingForResponse = state.isWaitingResponse
        )
        Spacer(modifier = Modifier.padding(12.dp))
    }
}

@Composable
fun keyboardVisibility(): State<Boolean> {
    val keyboardVisibilityState = rememberSaveable { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardVisibilityState.value = keypadHeight > screenHeight * 0.15
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
    return keyboardVisibilityState
}

fun handleBackPress(
    context: Context,
    viewModel: ChatViewModel,
    state: ChatState,
    onNavigateUp: () -> Unit,
) {
    if (state.chatMessages.size > 2) {
        viewModel.saveChatHistory(context)
    } else {
        onNavigateUp()
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatContentPreview() {
    ZakatKuyTheme {
        Surface(
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            ChatContent(
                state = ChatState(),
                chatHistoryId = "null",
                lazyListState = rememberLazyListState(),
                onPayZakatMalClick = { _, _ -> },
                onPayZakatFitrahClick = {},
                onChipClick = {},
                onTextChange = {},
                onSendClick = {},
                onVoiceClick = {}
            )
        }
    }
}