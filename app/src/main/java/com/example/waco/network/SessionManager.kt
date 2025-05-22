package com.example.waco.network

import android.content.Context

object SessionManager {

    fun getUsername(context: Context): String {
        val userPrefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val adminPrefs = context.getSharedPreferences("admin_data", Context.MODE_PRIVATE)

        return userPrefs.getString("username", null)
            ?: adminPrefs.getString("username", null)
            ?: "Gość"
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val userPrefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val adminPrefs = context.getSharedPreferences("admin_data", Context.MODE_PRIVATE)
        val userId = userPrefs.getString("user_id", null) ?: adminPrefs.getString("user_id", null)
        val email = userPrefs.getString("email", null) ?: adminPrefs.getString("email", null)
        val token = userPrefs.getString("firebase_token", null) ?: adminPrefs.getString("firebase_token", null)
        return userId != null && email != null && token != null
    }

    fun isAdmin(context: Context): Boolean {
        val adminPrefs = context.getSharedPreferences("admin_data", Context.MODE_PRIVATE)
        return adminPrefs.contains("user_id")
    }

    fun logout(context: Context) {
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences("admin_data", Context.MODE_PRIVATE).edit().clear().apply()
    }
}
