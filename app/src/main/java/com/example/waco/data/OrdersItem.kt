package com.example.waco.data

import com.google.gson.annotations.SerializedName


//Użytkownik orderItem
data class OrdersItem(
    val id: Int,
    val orderId: String,

    @SerializedName("product_name")
    val productName: String,
    val quantity: Double,
    val price: Double,
    val totalPrice: Double
)
