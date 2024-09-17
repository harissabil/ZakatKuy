package com.harissabil.zakatkuy.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.io.IOException
import java.util.Locale

fun getReadableLocation(latitude: Double?, longitude: Double?, context: Context): String? {
    var addressText: String? = null
    val geocoder = Geocoder(context, Locale.getDefault())

    if (latitude == null || longitude == null) {
        return null
    }

    try {

        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]
            addressText = address.getAddressLine(0)
            // Use the addressText in your app
            Timber.tag("geolocation").d(addressText)
        }

    } catch (e: IOException) {
        Timber.tag("geolocation").d(e.message.toString())
    }

    return addressText
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}