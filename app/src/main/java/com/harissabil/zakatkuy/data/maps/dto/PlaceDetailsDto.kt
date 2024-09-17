package com.harissabil.zakatkuy.data.maps.dto

import com.google.gson.annotations.SerializedName

data class PlaceDetailsDto(

    @field:SerializedName("result")
    val result: Result? = null,

    @field:SerializedName("html_attributions")
    val htmlAttributions: List<Any?>? = null,

    @field:SerializedName("status")
    val status: String? = null,
)

data class PhotosItemPd(

    @field:SerializedName("photo_reference")
    val photoReference: String? = null,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("html_attributions")
    val htmlAttributions: List<String?>? = null,

    @field:SerializedName("height")
    val height: Int? = null,
)

data class Result(

    @field:SerializedName("formatted_address")
    val formattedAddress: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("rating")
    val rating: Double? = null,

    @field:SerializedName("photos")
    val photos: List<PhotosItemPd?>? = null,

    @field:SerializedName("formatted_phone_number")
    val formattedPhoneNumber: String? = null,

    @field:SerializedName("international_phone_number")
    val internationalPhoneNumber: String? = null,
)
