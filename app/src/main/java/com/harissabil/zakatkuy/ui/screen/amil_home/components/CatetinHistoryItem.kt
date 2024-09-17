package com.harissabil.zakatkuy.ui.screen.amil_home.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harissabil.zakatkuy.data.firestore.models.ZakatDocumentation
import com.harissabil.zakatkuy.utils.toFormattedString

@Composable
fun CatetinHistoryItem(
    modifier: Modifier = Modifier,
    zakatDocumentation: ZakatDocumentation,
    onZakatDocumentationClick: (ZakatDocumentation) -> Unit,
) {
    OutlinedCard(onClick = { onZakatDocumentationClick(zakatDocumentation) }) {
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
                text = zakatDocumentation.tanggal_pembayaran?.toDate()!!.toFormattedString(),
                fontSize = 16.sp
            )
        }
    }
}