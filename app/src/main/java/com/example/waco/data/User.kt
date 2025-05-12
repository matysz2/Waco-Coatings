package com.example.waco.data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val message: String,
    val fcmToken: String? = null,
    val adminId: Int? = null,        // Dodane pole adminId jako Int?
    val prices: String? = null       // Jeśli też chcesz trzymać prices z user_admin
)
