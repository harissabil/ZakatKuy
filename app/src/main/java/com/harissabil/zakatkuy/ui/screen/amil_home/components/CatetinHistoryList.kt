package com.harissabil.zakatkuy.ui.screen.amil_home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harissabil.zakatkuy.data.firestore.models.ZakatDocumentation

@Composable
fun ColumnScope.CatetinHistoryList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    zakatDocumentationList: List<ZakatDocumentation>,
    onZakatDocumentationClick: (ZakatDocumentation) -> Unit,
) {
    if (zakatDocumentationList.isEmpty()) {
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
                text = "Tidak ada riwayat catetin,\nmulai nyatet sekarang!",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(modifier),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                items = zakatDocumentationList.sortedByDescending { it.tanggal_pembayaran },
                key = { it.id.toString() }) { chatHistory ->
                CatetinHistoryItem(
                    zakatDocumentation = chatHistory,
                    onZakatDocumentationClick = onZakatDocumentationClick
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}