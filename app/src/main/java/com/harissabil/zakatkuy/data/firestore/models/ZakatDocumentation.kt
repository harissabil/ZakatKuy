package com.harissabil.zakatkuy.data.firestore.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ZakatDocumentation(
    @DocumentId
    val id: String? = null,
    val email: String? = null,
    val nama_yang_mewakili: String? = null,
    val nama_pembayar: String? = null,
    val nama_yang_dibayarkan_zakat: String? = null,
    val alamat_pembayar: String? = null,
    val tanggal_pembayaran: Timestamp? = null,
    val nominal_zakat: Long? = null,
    val jenis_zakat: String? = null,
    val keterangan: String? = null,
)
