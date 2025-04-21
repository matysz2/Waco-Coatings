package com.example.waco.data

data class OrderItem(
    val productName: String,
    val quantity: Int,
    val comment: String,

    val createdAt: String?
)
