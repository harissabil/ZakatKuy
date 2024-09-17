package com.harissabil.zakatkuy.data.midtrans

import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.midtrans.dto.RequestLinkDto
import com.harissabil.zakatkuy.data.midtrans.models.DynamicPaymentLinkRequest
import com.harissabil.zakatkuy.data.midtrans.models.PaymentLinkRequest
import javax.inject.Inject
import javax.inject.Named

class MidtransRepository @Inject constructor(
    @Named("midtrans_api_service") private val apiService: ApiService,
) {

    suspend fun createPaymentLink(paymentLinkRequest: PaymentLinkRequest): Resource<RequestLinkDto> =
        try {
            val response = apiService.createPaymentLink(paymentLinkRequest)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }

    suspend fun createDynamicPaymentLink(dynamicPaymentLinkRequest: DynamicPaymentLinkRequest): Resource<RequestLinkDto> =
        try {
            val response = apiService.createDynamicPaymentLink(dynamicPaymentLinkRequest)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
}