package com.harissabil.zakatkuy.data.midtrans.models

data class TransactionDetails(
    val order_id: String,
    val gross_amount: Int,
    val payment_link_id: String
)

data class CreditCard(
    val secure: Boolean
)

data class Expiry(
    val duration: Int,
    val unit: String
)

data class ItemDetails(
    val id: String,
    val name: String,
    val price: Int,
    val quantity: Int
)

data class CustomerDetails(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
    val notes: String
)

data class PaymentLinkRequest(
    val transaction_details: TransactionDetails,
    val credit_card: CreditCard,
    val usage_limit: Int,
    val expiry: Expiry,
    val item_details: List<ItemDetails>,
    val customer_details: CustomerDetails
)
