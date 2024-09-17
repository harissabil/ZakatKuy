package com.harissabil.zakatkuy.ui.screen.form

import com.google.gson.annotations.SerializedName

data class KtpData(
	@field:SerializedName("kewarganegaraan")
	val kewarganegaraan: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("rtrw")
	val rtrw: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("pekerjaan")
	val pekerjaan: String? = null,

	@field:SerializedName("kecamatan")
	val kecamatan: String? = null,

	@field:SerializedName("agama")
	val agama: String? = null,

	@field:SerializedName("statusPerkawinan")
	val statusPerkawinan: String? = null,

	@field:SerializedName("jenisKelamin")
	val jenisKelamin: String? = null,

	@field:SerializedName("ttl")
	val ttl: String? = null,

	@field:SerializedName("kelurahan")
	val kelurahan: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null
)
