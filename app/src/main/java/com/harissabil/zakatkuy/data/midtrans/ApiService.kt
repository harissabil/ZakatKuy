package com.harissabil.zakatkuy.data.midtrans

import com.harissabil.zakatkuy.BuildConfig
import com.harissabil.zakatkuy.data.midtrans.dto.RequestLinkDto
import com.harissabil.zakatkuy.data.midtrans.models.DynamicPaymentLinkRequest
import com.harissabil.zakatkuy.data.midtrans.models.PaymentLinkRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "Authorization: Basic ${MidtransHelper.API_KEY}"
    )
    @POST("v1/payment-links")
    suspend fun createPaymentLink(@Body paymentLinkRequest: PaymentLinkRequest): Response<RequestLinkDto>

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "Authorization: Basic ${MidtransHelper.API_KEY}"
    )
    @POST("v1/payment-links")
    suspend fun createDynamicPaymentLink(
        @Body dynamicPaymentLinkRequest: DynamicPaymentLinkRequest,
    ): Response<RequestLinkDto>
}

object MidtransHelper {
    const val API_KEY = BuildConfig.MIDTRANS_API_KEY
}