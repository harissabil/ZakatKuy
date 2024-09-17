package com.harissabil.zakatkuy.ui.screen.chat.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harissabil.zakatkuy.data.firestore.models.ChatMessage
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun ColumnScope.ChatList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    chatList: List<ChatMessage>,
    onPayZakatMalClick: (amount: Long, category: String?) -> Unit,
    onPayZakatFitrahClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .then(modifier),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        items(items = chatList.sortedBy { it.order }, key = { it.id!! }) { chat ->
            ChatItem(
                modifier = Modifier.animateItem(),
                chatMessage = chat,
                onPayZakatMalClick = onPayZakatMalClick,
                onPayZakatFitrahClick = onPayZakatFitrahClick
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatListPreview() {
    ZakatKuyTheme {
        Surface {
            Column {
                ChatList(
                    lazyListState = rememberLazyListState(),
                    chatList = listOf(
                        ChatMessage(
                            id = "1",
                            message = "1Halo, saya ingin bertanya tentang zakat",
                            isUser = true
                        ),
                        ChatMessage(
                            id = "2",
                            message = "2Halo, saya ingin bertanya tentang zakat",
                            isUser = false
                        ),
                        ChatMessage(
                            id = "3",
                            message = "3Halo, saya ingin bertanya tentang zakat",
                            isUser = true
                        ),
                        ChatMessage(
                            id = "4",
                            message = "4Halo, saya ingin bertanya tentang zakat",
                            isUser = false
                        ),
                        ChatMessage(
                            id = "5",
                            message = "5Halo, saya ingin bertanya tentang zakat",
                            isUser = true
                        ),
                        ChatMessage(
                            id = "6",
                            message = "6Halo, saya ingin bertanya tentang zakat",
                            isUser = false
                        ),
                        ChatMessage(
                            id = "7",
                            message = "7Halo, saya ingin bertanya tentang zakat",
                            isUser = true
                        ),
                        ChatMessage(
                            id = "8",
                            message = "Halo8, saya ingin bertanya tentang zakat",
                            isUser = false
                        ),
                        ChatMessage(
                            id = "9",
                            message = "9Halo, saya ingin bertanya tentang zakat",
                            isUser = true
                        ),
                        ChatMessage(
                            id = "10",
                            message = "10Halo, saya ingin bertanya tentang zakat",
                            isUser = false
                        ),
                    ),
                    onPayZakatMalClick = { amount, category -> },
                    onPayZakatFitrahClick = {}
                )
            }
        }
    }
}