package com.harissabil.zakatkuy.ui.screen.chat.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun ChatRecommendationChip(
    modifier: Modifier = Modifier,
    onChipClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .then(modifier)
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        AssistChip(
            shape = RoundedCornerShape(16.dp),
            colors = AssistChipDefaults.assistChipColors().copy(
                containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                labelColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            onClick = { onChipClick("Mau bayar zakat fitrah") },
            label = {
                Text(
                    text = "Mau bayar zakat fitrah",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        AssistChip(
            shape = RoundedCornerShape(16.dp),
            colors = AssistChipDefaults.assistChipColors().copy(
                containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                labelColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            onClick = { onChipClick("Mau bayar zakat mal") },
            label = {
                Text(
                    text = "Mau bayar zakat mal",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                )
            }
        )
        Spacer(modifier = Modifier.width(24.dp))
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatRecommendationChipPreview() {
    ZakatKuyTheme {
        Surface {
            ChatRecommendationChip(
                onChipClick = {}
            )
        }
    }
}