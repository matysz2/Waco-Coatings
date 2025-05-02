package com.example.waco.data


data class UserAdmin(
    val id: String,
    val login: String,
    val email: String,
    val name: String?,
    val surname: String?,
    val prices: String?,
    val fcm_token: String?
)
