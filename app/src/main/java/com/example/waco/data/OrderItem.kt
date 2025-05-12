package com.example.waco.data

import com.google.gson.annotations.SerializedName

//Admin OrderItem
data class OrderItem(
    val id: Int,
    val orderId: String,

    val productName: String,
    val quantity: Double,
    val price: Double,
    val totalPrice: Double
)
