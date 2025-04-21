package com.example.waco

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val firebaseApp = FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "Firebase initialized: ${firebaseApp != null}")
    }
}
