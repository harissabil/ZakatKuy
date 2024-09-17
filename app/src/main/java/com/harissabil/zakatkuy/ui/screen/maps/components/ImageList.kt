package com.harissabil.zakatkuy.ui.screen.maps.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harissabil.zakatkuy.BuildConfig
import com.harissabil.zakatkuy.data.maps.dto.PhotosItemPd
import timber.log.Timber

@Composable
fun ImageList(
    modifier: Modifier = Modifier,
    photos: List<PhotosItemPd?>,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = photos, key = { it?.photoReference!! }) { photo ->
            Timber.d("Link gambar: https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo?.photoReference}&key=${BuildConfig.MAPS_API_KEY}")
            AsyncImage(
                modifier = Modifier.height(120.dp),
                model = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo?.photoReference}&key=${BuildConfig.MAPS_API_KEY}",
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ImageListPreview() {

}