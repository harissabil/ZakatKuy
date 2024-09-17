package com.harissabil.zakatkuy.data.gemini

import retrofit2.Response
import retrofit2.http.GET

interface GoldPriceService {
    @GET("prices/hargaemas-org")
    suspend fun getGoldPrices(): Response<GoldPriceResponse>
}