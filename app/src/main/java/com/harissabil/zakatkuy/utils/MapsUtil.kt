package com.harissabil.zakatkuy.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState

suspend fun CameraPositionState.centerOnLocation(
    location: LatLng,
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(location, 17.5f),
    durationMs = 500
)

fun GeoPoint.toLatLng() = LatLng(latitude, longitude)

fun convertPhoneNumberToWhatsAppLink(phoneNumber: String): String {
    // Remove non-digit characters except '+' at the start
    val cleanedNumber = phoneNumber.replace("[^\\d+]".toRegex(), "")

    // Check if the phone number starts with '+' and replace with 'wa.me/' and remove '+'
    return if (cleanedNumber.startsWith("+")) {
        "https://wa.me/" + cleanedNumber.substring(1) // Remove the '+'
    } else {
        "https://wa.me/$cleanedNumber"
    }
}