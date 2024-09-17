package com.harissabil.zakatkuy.data.midtrans.models

data class DynamicPaymentLinkRequest(
    val transaction_details: TransactionDetails,
    val credit_card: CreditCard,
    val usage_limit: Int,
    val expiry: Expiry,
    val customer_details: CustomerDetails,
    val dynamic_amount: DynamicAmount,
    val payment_link_type: String,
)

data class DynamicAmount(
    val min_amount: Int = 1,
    val max_amout: Int? = null,
    val preset_amount: Int = 1,
)