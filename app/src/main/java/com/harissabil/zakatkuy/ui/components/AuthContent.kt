package com.harissabil.zakatkuy.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harissabil.zakatkuy.R
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun AuthContent(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    buttonTitle: String,
    bottomText: String,
    clickableText: String,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onContinueWithGoogleClick: () -> Unit,
    onSignInClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .then(modifier),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                text = subtitle,
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_content"),
            ) {
                Text(text = "Email Address", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                MyTextFieldComponent(
                    placeHolder = "Email Address",
                    icon = Icons.Filled.Email,
                    textValue = email,
                    onValueChange = onEmailChange,
                    isEmailField = true
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Password", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                PasswordTextFieldComponent(
                    placeHolder = "Password",
                    icon = Icons.Filled.Lock,
                    textValue = password,
                    onValueChange = onPasswordChange,
                )

                Spacer(modifier = Modifier.height(24.dp))
                CommonButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = buttonTitle,
                ) {
                    onRegisterClick()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("or_divider"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFB1A9A9))
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "or", fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFB1A9A9))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("social_login"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SignInWithButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Continue with Google",
                    icon = R.drawable.logo_google,
                    onClick = onContinueWithGoogleClick
                )
                SignInWithButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Continue with Facebook",
                    icon = R.drawable.logo_facebook,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bottomText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.clickable {
                            onSignInClick()
                        },
                        text = " $clickableText",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AuthContentPreview() {
    ZakatKuyTheme {
        Surface {
            AuthContent(
                title = "Ahlan wa sahlan!",
                subtitle = "Sign up to get started",
                buttonTitle = "Sign Up",
                bottomText = "Already have an account?",
                clickableText = "Sign In",
                email = "",
                onEmailChange = {},
                password = "",
                onPasswordChange = {},
                onRegisterClick = {},
                onContinueWithGoogleClick = {},
                onSignInClick = {}
            )
        }
    }
}