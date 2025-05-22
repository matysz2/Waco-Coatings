package com.example.waco.data

data class ProductDetailResponse(
    val description: String,
    val imageUrl: String,
    val comments: List<Comment>
)
