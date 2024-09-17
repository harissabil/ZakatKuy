package com.harissabil.zakatkuy.ui.screen.chat.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harissabil.zakatkuy.R
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun ChatTextField(
    modifier: Modifier = Modifier,
    textValue: String,
    isWaitingForResponse: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onVoiceClick: () -> Unit,
) {
    OutlinedTextField(
        value = textValue,
        onValueChange = onTextChange,
        shape = RoundedCornerShape(30.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(30.dp)),
        placeholder = {
            Text(text = "Tanya Zaki di sini!")
        },
        trailingIcon = {
            Row(
                modifier = Modifier.padding(end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                IconButton(onClick = onVoiceClick) {
                    Icon(
                        modifier = Modifier.clickable { onVoiceClick() },
                        painter = painterResource(R.drawable.ic_graphic_eq),
                        contentDescription = null,
                        tint = Color(0xFFB1DEB9)
                    )
//                }
                if (!isWaitingForResponse) {
                    IconButton(onClick = { onSendClick(textValue) }) {
                        Icon(
                            modifier = Modifier.clickable { onSendClick(textValue) },
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(12.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp,
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                }
            }
        },
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            focusedContainerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        ),
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatTextFieldPreview() {
    ZakatKuyTheme {
        Surface(
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ChatTextField(
                    textValue = "",
                    onTextChange = {},
                    onSendClick = {},
                    onVoiceClick = {},
                    isWaitingForResponse = true
                )
            }
        }
    }
}