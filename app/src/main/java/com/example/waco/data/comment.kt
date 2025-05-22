package com.example.waco.data
data class Comment(
    val id: Int,
    val product_id: Int,
    val username: String,
    val content: String,
    val rating: Int,
    val created_at: String
)
