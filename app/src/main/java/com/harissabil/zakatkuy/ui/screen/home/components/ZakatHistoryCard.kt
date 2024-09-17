package com.harissabil.zakatkuy.ui.screen.home.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun ZakatHistoryCard(
    modifier: Modifier = Modifier,
    onHistoryClick: () -> Unit,
    zakatTotal: Long?,
) {
    var isMoneyVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Alhamdulillah,", fontSize = 16.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Rp. ${if (isMoneyVisible) "%,d".format(zakatTotal ?: 0) else "*****"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                )
                IconButton(onClick = {
                    isMoneyVisible = !isMoneyVisible
                }) {
                    Icon(
                        imageVector = if (isMoneyVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            Text(text = "sudah dizakatkan!", fontSize = 14.sp)

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onHistoryClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = "Riwayat")
                Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ZakatHistoryCardPreview() {
    ZakatKuyTheme {
        Surface {
            ZakatHistoryCard(
                zakatTotal = 10000000000,
                onHistoryClick = {}
            )
        }
    }
}