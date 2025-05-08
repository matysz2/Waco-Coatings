package com.example.waco.data

data class Order(
    val orderId: Int,
    val userId: Int,
    val orderDate: String,
    val status: String,
    val comment: String,
    val price: Double,
    val createdAt: String,
    val items: List<OrderItem>
)
