package com.harissabil.zakatkuy.data.maps

import com.google.android.gms.maps.model.LatLng
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.maps.api.MapsService
import com.harissabil.zakatkuy.data.maps.dto.NearbySearchDto
import com.harissabil.zakatkuy.data.maps.dto.PlaceDetailsDto
import com.harissabil.zakatkuy.data.maps.model.Route
import javax.inject.Inject

class MapsRepository @Inject constructor(
    private val mapsService: MapsService,
) {
    suspend fun getDirections(origin: LatLng, destination: LatLng): Resource<Route> {
        return try {

            val response = mapsService.getDirections(
                originLatLng = "${origin.latitude},${origin.longitude}",
                destinationLatLang = "${destination.latitude},${destination.longitude}",
            )


            if (response.isSuccessful && response.body() != null) {

                val polyLinePoints = try {
                    response.body()!!.routes[0].legs[0].steps.map { step ->
                        step.polyline.decodePolyline(step.polyline.points)
                    }
                } catch (e: Exception) {
                    emptyList()
                }
                Resource.Success(data = Route(routePoints = polyLinePoints))
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    suspend fun getNearbyMosques(location: LatLng): Resource<NearbySearchDto> {
        return try {
            val response = mapsService.getNearbyMosques(
                location = "${location.latitude},${location.longitude}",
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(data = response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }

    suspend fun getPlaceDetails(placeId: String): Resource<PlaceDetailsDto> {
        return try {
            val response = mapsService.getPlaceDetails(
                placeId = placeId,
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(data = response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }
}