package com.harissabil.zakatkuy.ui.screen.form

data class FormState(
    val nik: String? = "",
    val nama: String? = "",
    val ttl: String? = "",
    var jenisKelamin: String? = "",
    val alamat: String? = "",
    val rtrw: String? = "",
    val kelurahan: String? = "",
    val kecamatan: String? = "",
    val agama: String? = "",
    var statusPerkawinan: String? = "",
    val pekerjaan: String? = "",
    val kewarganegaraan: String? = "",
    var tujuanZakat: String? = "",

    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
)
