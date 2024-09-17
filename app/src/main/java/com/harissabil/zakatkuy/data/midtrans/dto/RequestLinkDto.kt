package com.harissabil.zakatkuy.data.midtrans.dto

import com.google.gson.annotations.SerializedName

data class RequestLinkDto(

	@field:SerializedName("payment_url")
	val paymentUrl: String,

	@field:SerializedName("order_id")
	val orderId: String
)
