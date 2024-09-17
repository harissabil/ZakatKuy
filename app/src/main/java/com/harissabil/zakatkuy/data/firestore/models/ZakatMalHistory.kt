package com.harissabil.zakatkuy.data.firestore.models

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

@Keep
data class ZakatMalHistory(
    @DocumentId
    val id: String? = null,

    @get:JvmName("midtrans_id")
    @set:JvmName("midtrans_id")
    var midtransId: String? = null,

    val email: String? = null,

    val timestamp: Timestamp? = null,

    val amount: Long? = null,

    val category: String? = null,

    val status: String? = null,
)