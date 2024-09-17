package com.harissabil.zakatkuy.ui.screen.home.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.harissabil.zakatkuy.data.firestore.models.ChatHistory
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme
import com.harissabil.zakatkuy.utils.toFormattedString

@Composable
fun ChatHistoryItem(
    modifier: Modifier = Modifier,
    chatHistory: ChatHistory,
    onChatHistoryClick: (chatHistory: ChatHistory) -> Unit,
) {
    OutlinedCard(onClick = { onChatHistoryClick(chatHistory) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .then(modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Chat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = chatHistory.timestamp?.toDate()!!.toFormattedString(),
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatHistoryItemPreview() {
    ZakatKuyTheme {
        Surface {
            ChatHistoryItem(
                chatHistory = ChatHistory(
                    timestamp = Timestamp.now()
                ),
                onChatHistoryClick = {}
            )
        }
    }
}