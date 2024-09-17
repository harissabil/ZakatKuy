package com.harissabil.zakatkuy.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toFormattedString(): String {
    val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    return format.format(this)
}