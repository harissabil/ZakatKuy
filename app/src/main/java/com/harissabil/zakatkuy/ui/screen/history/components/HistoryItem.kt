package com.harissabil.zakatkuy.ui.screen.history.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.harissabil.zakatkuy.data.firestore.models.ZakatMalHistory
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme
import com.harissabil.zakatkuy.utils.toFormattedString

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    zakatMalHistory: ZakatMalHistory,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val zakatCategory = mapOf(
        "zakatMal" to "Zakat Mal",
        "zakatPenghasilan" to "Zakat Penghasilan",
        "zakatFitrah" to "Zakat Fitrah",
        "zakatPerdagangan" to "Zakat Perdagangan",
        "zakatEmas" to "Zakat Emas",
        "zakatSaham" to "Zakat Saham",
        "zakatTernak" to "Zakat Ternak",
        null to "Zakat Lainnya"
    )

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = zakatCategory[zakatMalHistory.category] ?: "Zakat Lainnya",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
                zakatMalHistory.timestamp?.toDate()?.let {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        text = it.toFormattedString(),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Rp. ${"%,d".format(zakatMalHistory.amount)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DotProgress(
                        modifier = Modifier.weight(1f),
                        value = "Menunggu",
                        isDone = zakatMalHistory.status == "pending" || zakatMalHistory.status == "accepted" || zakatMalHistory.status == "sent" || zakatMalHistory.status == "received"
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(0.5f),
                        color = if (zakatMalHistory.status == "accepted" || zakatMalHistory.status == "sent" || zakatMalHistory.status == "received")
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    )
                    DotProgress(
                        modifier = Modifier.weight(1f),
                        value = "Diterima",
                        isDone = zakatMalHistory.status == "accepted" || zakatMalHistory.status == "sent" || zakatMalHistory.status == "received"
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(0.5f),
                        color = if (zakatMalHistory.status == "sent" || zakatMalHistory.status == "received")
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    )
                    DotProgress(
                        modifier = Modifier.weight(1f),
                        value = "Disalurkan",
                        isDone = zakatMalHistory.status == "sent" || zakatMalHistory.status == "received"
                    )
                }
            }
        }
    }
}

@Composable
fun DotProgress(
    modifier: Modifier = Modifier,
    value: String,
    isDone: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(
                    color = if (isDone) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            color = if (isDone) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HistoryItemPreview() {
    ZakatKuyTheme {
        Surface {
            HistoryItem(
                zakatMalHistory = ZakatMalHistory(
                    amount = 100000,
                    category = "zakatMal",
                    timestamp = Timestamp.now(),
                    status = "accepted"
                )
            )
        }
    }
}