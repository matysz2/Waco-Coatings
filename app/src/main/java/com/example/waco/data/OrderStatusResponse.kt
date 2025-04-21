package com.example.waco.data

data class OrderStatusResponse(
    val orderId: Int,
    val status: String,
    val comment: String?
)
