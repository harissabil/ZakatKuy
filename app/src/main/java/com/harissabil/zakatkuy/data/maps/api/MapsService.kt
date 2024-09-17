package com.harissabil.zakatkuy.data.maps.api

import com.harissabil.zakatkuy.BuildConfig
import com.harissabil.zakatkuy.data.maps.dto.DirectionsDto
import com.harissabil.zakatkuy.data.maps.dto.NearbySearchDto
import com.harissabil.zakatkuy.data.maps.dto.PlaceDetailsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsService {

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") originLatLng: String,
        @Query("destination") destinationLatLang: String,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY,
    ): Response<DirectionsDto>

    @GET("place/nearbysearch/json")
    suspend fun getNearbyMosques(
        @Query("location") location: String, // e.g. "-6.566015,106.740151"
        @Query("radius") radius: Int = 5000,  // default radius in meters
        @Query("type") type: String = "mosque",
        @Query("keyword") keyword: String = "Zakat",
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY,
    ): Response<NearbySearchDto>

    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String, // Place ID from the Nearby Search
        @Query("fields") fields: String = "name,rating,formatted_phone_number,international_phone_number,formatted_address,photos",
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY,
    ): Response<PlaceDetailsDto>
}