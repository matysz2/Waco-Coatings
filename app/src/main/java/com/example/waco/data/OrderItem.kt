package com.example.waco.data

import com.google.gson.annotations.SerializedName

//Admin OrderItem
data class OrderItem(
    val id: Int,
    val orderId: String,

    @SerializedName("product_name")
    val productName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double
)
