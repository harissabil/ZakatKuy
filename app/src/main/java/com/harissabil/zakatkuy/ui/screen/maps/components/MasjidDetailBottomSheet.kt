package com.harissabil.zakatkuy.ui.screen.maps.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harissabil.zakatkuy.data.maps.dto.PlaceDetailsDto
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasjidDetailBottomSheet(
    modifier: Modifier = Modifier,
    masjidDetail: PlaceDetailsDto,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onPhoneNumClick: (phoneNumber: String?) -> Unit,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        shape = RectangleShape
    ) {
        MasjidDetailBottomSheetContent(
            modifier = modifier,
            masjidDetail = masjidDetail,
            onPhoneNumClick = onPhoneNumClick
        )
    }
}

@Composable
fun MasjidDetailBottomSheetContent(
    modifier: Modifier = Modifier,
    masjidDetail: PlaceDetailsDto,
    onPhoneNumClick: (phoneNumber: String?) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .shadow(6.dp, shape = RoundedCornerShape(12.dp))
            .then(modifier),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (masjidDetail.result?.photos != null) {
                ImageList(photos = masjidDetail.result.photos)
            }
            Text(
                text = masjidDetail.result?.name!!,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
            )
            Text(
                text = masjidDetail.result.formattedAddress!!,
                fontSize = 14.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = masjidDetail.result.internationalPhoneNumber
                        ?: masjidDetail.result.formattedPhoneNumber
                        ?: "Nomor telepon tidak tersedia",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                )
                IconButton(
                    onClick = {
                        onPhoneNumClick(
                            masjidDetail.result.internationalPhoneNumber
                                ?: masjidDetail.result.formattedPhoneNumber
                        )
                    },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Chat,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MasjidDetailBottomSheetPreview() {
    ZakatKuyTheme {
        Surface {
            MasjidDetailBottomSheetContent(
                masjidDetail = PlaceDetailsDto(),
                onPhoneNumClick = { /*TODO*/ }
            )
        }
    }
}