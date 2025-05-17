package com.example.waco.data

data class Invoice(
    val id: Int,
    val user_id: Int,
    val invoice_number: String,
    val date: String,
    val amount: String,
    val status: String,
    val gross_value: Double,
    val link: String?
)
