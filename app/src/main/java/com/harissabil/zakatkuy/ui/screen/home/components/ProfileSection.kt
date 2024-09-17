package com.harissabil.zakatkuy.ui.screen.home.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDownCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.harissabil.zakatkuy.R
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier,
    avatarUrl: String?,
    name: String?,
    onDropDownClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (avatarUrl == null || avatarUrl == "null") {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.ic_generic_avatar),
                contentDescription = null,
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = avatarUrl,
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.alpha(0.5f),
                text = "Ahlan wa sahlan,",
                fontSize = 14.sp,
                lineHeight = 4.sp
            )
            Text(
                text = name ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }

        IconButton(
            onClick = onDropDownClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowDropDownCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileSectionPreview() {
    ZakatKuyTheme {
        Surface {
            ProfileSection(
                modifier = Modifier.padding(horizontal = 16.dp),
                avatarUrl = null,
                name = "Muhammad Fulan bin Muhammad Fulan",
                onDropDownClick = { }
            )
        }
    }
}