package com.example.waco.model

import com.example.waco.data.UserAdmin


data class LoginResponse(
    val status: String,
    val user: UserAdmin?
)
