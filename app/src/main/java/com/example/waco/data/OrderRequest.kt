package com.example.waco.data



import com.google.gson.annotations.SerializedName

data class OrderRequest(
    val userId: String,
    val email: String,
    val comment: String,
    val products: List<ProductItem>,
    val prices: String? = null // ← nowy parametr

)