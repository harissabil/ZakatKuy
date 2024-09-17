package com.harissabil.zakatkuy.ui.screen.form.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    textValue: String,
    onValueChange: (String) -> Unit,
    placeHolder: String,
    conditionCheck: (String) -> Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Text(
            modifier = Modifier.padding(start = 48.dp),
            text = placeHolder, fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = if (conditionCheck(textValue)) {
                    Icons.Filled.CheckCircle
                } else Icons.Outlined.CheckCircle,
                tint = if (conditionCheck(textValue)) {
                    MaterialTheme.colorScheme.primary
                } else Color(0xFFB4AFAF),
                contentDescription = null
            )
            OutlinedTextField(
                value = textValue,
                onValueChange = onValueChange,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = placeHolder, color = Color(0xFFB4AFAF))
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FormTextFieldPreview() {
    ZakatKuyTheme {
        Surface(
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            var value by remember { mutableStateOf("") }
            FormTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                textValue = value,
                onValueChange = { value = it },
                placeHolder = "Email Address",
                conditionCheck = { it.contains("@") && it.contains(".") }
            )
        }

    }
}