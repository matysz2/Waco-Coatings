package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.ui.fragments.CurrentOrderFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button

    private val loginUrl = "http://waco.atwebpages.com/waco/login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // Inicjalizacja Firebase
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "Firebase initialized: ${FirebaseApp.getInstance() != null}")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "LOGOWANIE"

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            loginUser(email, password)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("LoginActivity", "Kliknięto strzałkę powrotu")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    private fun loginUser(email: String, password: String) {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(loginUrl)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Błąd połączenia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    try {
                        println("ODPOWIEDŹ SERWERA: $responseBody")
                        val json = JSONObject(responseBody)

                        val status = json.optString("status")
                        if (status == "success") {
                            val userId = json.optString("user_id")
                            val userEmail = json.optString("email")

                            // Pobranie tokena z Firebase i zapisanie go
                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val token = task.result
                                    Log.d("FirebaseToken", "Nowy token: $token")

                                    val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                    sharedPref.edit().apply {
                                        putBoolean("is_logged_in", true)
                                        putString("user_id", userId)
                                        putString("email", userEmail)
                                        putString("firebase_token", token)
                                        apply()
                                    }

                                    // Wysyłka tokena na serwer
                                    updateFirebaseToken(userId, token)

                                    runOnUiThread {
                                        Toast.makeText(this@LoginActivity, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@LoginActivity, OrderActivity::class.java))
                                        finish()
                                    }
                                } else {
                                    runOnUiThread {
                                        Toast.makeText(this@LoginActivity, "Nie udało się pobrać tokena", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            val errorMsg = json.optString("message", "Nieznany błąd logowania")
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Błąd JSON: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }

    private fun updateFirebaseToken(userId: String, token: String) {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("user_id", userId)
            .add("fcm_token", token)
            .build()

        val request = Request.Builder()
            .url("http://waco.atwebpages.com/waco/save_fcm_token.php")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Błąd połączenia z aktualizacją tokenu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Błąd przy aktualizacji tokenu", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ -> super.onBackPressed() }
            .setNegativeButton("Nie") { dialog, _ -> dialog.dismiss() }

        val alert = builder.create()
        alert.show()
    }
}
