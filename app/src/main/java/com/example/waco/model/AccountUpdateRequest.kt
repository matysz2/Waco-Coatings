package com.example.waco.model

data class AccountUpdateRequest(
    val user_id: String,
    val name: String,
    val email: String,
    val password: String
)
