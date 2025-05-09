package com.example.waco.data

import com.google.gson.annotations.SerializedName


data class OrdersItem(
    val id: Int,
    val orderId: String,

    val productName: String,

    val quantity: Int,
    val price: Double,
    val totalPrice: Double
)
