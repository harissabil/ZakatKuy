package com.harissabil.zakatkuy.ui.screen.chat.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harissabil.zakatkuy.R
import com.harissabil.zakatkuy.data.firestore.models.ChatMessage
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    chatMessage: ChatMessage,
    onPayZakatMalClick: (amount: Long, category: String?) -> Unit,
    onPayZakatFitrahClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = if (chatMessage.isUser == true) {
            Arrangement.End
        } else {
            Arrangement.Start
        },
    ) {
        if (chatMessage.isUser == true) {
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.25f))
        }
        if (chatMessage.isUser == false) {
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.ic_generic_avatar),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(
            modifier = Modifier
                .background(
                    color = if (chatMessage.isUser == true) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
                    shape = RoundedCornerShape(
                        topStart = if (chatMessage.isUser == true) 20.dp else 0.dp,
                        topEnd = if (chatMessage.isUser == true) 0.dp else 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(12.dp),
        ) {
            Text(
                text = chatMessage.message!!,
                color = if (chatMessage.isUser == true) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
            )
            if (chatMessage.isUser == false) {
                if (chatMessage.zakatMalToPay != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            onPayZakatMalClick(
                                chatMessage.zakatMalToPay!!,
                                chatMessage.zakatCategory
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,

                            ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(text = "Bayar sekarang!")
                    }
                }
                if (chatMessage.isGoingToPayZakatFitrah == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        shape = RoundedCornerShape(12.dp),
                        onClick = onPayZakatFitrahClick,
                        colors = ButtonDefaults.outlinedButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,

                            ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(text = "Navigasi")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatItemPrev() {
    ZakatKuyTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ChatItem(
                    chatMessage = ChatMessage(
                        message = "Assalamualaikum akhi! Ana Zaki, asisten virtual antum dalam membantu perhitungan zakat dan menjawab pertanyaan seputar zakat. Ana siap membantu menghitung zakat penghasilan, zakat emas, zakat perdagangan, zakat fitrah, zakat ternak, dan jenis zakat lainnya. Silakan tanyakan apa pun tentang zakat, insyaAllah ana siap membantu!",
                        isUser = false,
                        isGoingToPayZakatFitrah = true,
                    ),
                    onPayZakatMalClick = { amount, category ->

                    },
                    onPayZakatFitrahClick = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
                ChatItem(
                    chatMessage = ChatMessage(
                        message = "Assalamualaikum akhi! Ana Zaki, asisten virtual antum dalam membantu perhitungan zakat dan menjawab pertanyaan seputar zakat. Ana siap membantu menghitung zakat penghasilan, zakat emas, zakat perdagangan, zakat fitrah, zakat ternak, dan jenis zakat lainnya. Silakan tanyakan apa pun tentang zakat, insyaAllah ana siap membantu!",
                        isUser = true
                    ),
                    onPayZakatMalClick = { amount, category -> },
                    onPayZakatFitrahClick = {}
                )
            }
        }
    }
}