package com.example.waco.data

class ProductDescription (

    val id: Int,
    val name: String,
    val quantity: Double,
    val price: Double,
    val description: String = "",
    val imageUrl: String = "",
    val comments: List<String> = emptyList()

    )

