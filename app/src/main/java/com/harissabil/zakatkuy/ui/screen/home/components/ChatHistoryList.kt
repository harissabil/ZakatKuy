package com.harissabil.zakatkuy.ui.screen.home.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.harissabil.zakatkuy.data.firestore.models.ChatHistory
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun ColumnScope.ChatHistoryList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    chatHistoryList: List<ChatHistory>,
    onChatHistoryClick: (chatHistory: ChatHistory) -> Unit,
) {
    if (chatHistoryList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(72.dp)
                    .alpha(0.7f),
                imageVector = Icons.Outlined.HistoryEdu, contentDescription = null
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier.alpha(0.7f),
                text = "Tidak ada riwayat chat,\nmulai chat sekarang!",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                items = chatHistoryList.sortedByDescending { it.timestamp },
                key = { it.id.toString() }) { chatHistory ->
                ChatHistoryItem(
                    chatHistory = chatHistory,
                    onChatHistoryClick = onChatHistoryClick
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatHistoryListPreview() {
    ZakatKuyTheme {
        Surface {
            Column {
                ChatHistoryList(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    lazyListState = rememberLazyListState(),
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
                        ),
                    ),
                    onChatHistoryClick = { }
                )
            }
        }
    }
}