package com.example.waco.data

data class OrderDetails(
    val orderId: Int,
    val userId: Int,
    val comment: String,
    val status: String,
    val orderDate: String,
    val items: List<OrderItem>
)


