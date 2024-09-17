package com.harissabil.zakatkuy.data.firestore.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

@Keep
data class UserData(
    @DocumentId
    val id: String? = null,

    val email: String? = null,

    val nik: String? = null,

    val nama: String? = null,

    val ttl: String? = null,

    @get:PropertyName("jenis_kelamin")
    @set:PropertyName("jenis_kelamin")
    var jenisKelamin: String? = null,

    val alamat: String? = null,

    val rtrw: String? = null,

    val kelurahan: String? = null,

    val kecamatan: String? = null,

    val agama: String? = null,

    @get:PropertyName("status_perkawinan")
    @set:PropertyName("status_perkawinan")
    var statusPerkawinan: String? = null,

    val pekerjaan: String? = null,

    val kewarganegaraan: String? = null,

    @get:PropertyName("tujuan_zakat")
    @set:PropertyName("tujuan_zakat")
    var tujuanZakat: String? = null,
)
