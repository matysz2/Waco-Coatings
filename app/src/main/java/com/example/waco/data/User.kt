package com.example.waco.data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val message: String,
    val fcmToken: String? = null  // Dodajemy pole fcmToken, które może być null, ponieważ nie zawsze jest dostępne
)
