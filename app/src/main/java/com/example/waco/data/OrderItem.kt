package com.example.waco.data

data class OrderItem(
    val productName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
    val comment: String,
    val createdAt: String?
)
