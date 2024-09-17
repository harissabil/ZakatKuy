package com.harissabil.zakatkuy.ui.screen.maps

import com.google.android.gms.maps.model.LatLng
import com.harissabil.zakatkuy.data.maps.dto.PlaceDetailsDto
import com.harissabil.zakatkuy.data.maps.dto.ResultsItem
import com.harissabil.zakatkuy.data.maps.model.Route

data class MapsState(
    val currentLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val nearbyMasjid: List<ResultsItem?> = emptyList(),
    val routePoints: Route? = null,
    val selectedMasjid: ResultsItem? = null,
    val selectedMasjidDetail: PlaceDetailsDto? = null,
)