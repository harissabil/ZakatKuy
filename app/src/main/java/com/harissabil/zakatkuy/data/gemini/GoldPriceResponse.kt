package com.harissabil.zakatkuy.data.gemini

import com.google.gson.annotations.SerializedName

data class GoldPriceResponse(

	@field:SerializedName("data")
	val data: List<DataItem>? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)

data class Meta(

	@field:SerializedName("engine")
	val engine: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class DataItem(

	@field:SerializedName("sell")
	val sell: Int? = null,

	@field:SerializedName("buy")
	val buy: Int? = null,

	@field:SerializedName("type")
	val type: String? = null
)
