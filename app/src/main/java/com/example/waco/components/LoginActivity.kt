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
        // 1. Sprawdzenie czy administrator jest zalogowany
        val adminPrefs = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
        val adminId = adminPrefs.getString("user_id", null)
        val adminEmail = adminPrefs.getString("email", null)
        val adminToken = adminPrefs.getString("firebase_token", null)
        val price = adminPrefs.getString("price", null)

        if (!adminId.isNullOrEmpty() && !adminEmail.isNullOrEmpty() && !adminToken.isNullOrEmpty()) {
            Log.d("LoginActivity", "Zalogowany admin: $adminEmail")
            startActivity(Intent(this, OrderActivity::class.java)) // admin też trafia do OrderActivity
            finish()
            return
        }

// 2. Sprawdzenie czy zwykły użytkownik jest zalogowany
        val userPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = userPrefs.getString("user_id", null)
        val userEmail = userPrefs.getString("email", null)
        val userToken = userPrefs.getString("firebase_token", null)

        if (!userId.isNullOrEmpty() && !userEmail.isNullOrEmpty() && !userToken.isNullOrEmpty()) {
            Log.d("LoginActivity", "Zalogowany użytkownik: $userEmail")
            startActivity(Intent(this, OrderActivity::class.java))
            finish()
            return
        }
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
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Proszę wprowadzić email i hasło", Toast.LENGTH_SHORT).show()
            Log.w("LoginUser", "Puste dane logowania: email=$email, hasło=${password.length} znaków")
            return
        }

        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(loginUrl)
            .post(formBody)
            .build()

        Log.d("LoginUser", "Wysyłanie żądania logowania: $loginUrl")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("LoginUser", "Błąd połączenia: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Błąd połączenia: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("LoginUser", "Odpowiedź serwera (status=${response.code}): $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val json = JSONObject(responseBody)
                        val status = json.optString("status")
                        Log.d("LoginUser", "Status odpowiedzi JSON: $status")

                        if (status == "success") {
                            val userId = json.optString("user_id")
                            val userEmail = json.optString("email")
                            val adminId = json.optString("admin_id")
                            val prices = json.optString("prices")

                            Log.d("LoginUser", "Dane użytkownika: userId=$userId, email=$userEmail, adminId=$adminId")

                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val token = task.result
                                    Log.d("FirebaseToken", "Pobrano token: $token")

                                    val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                    sharedPref.edit().apply {
                                        putBoolean("is_logged_in", true)
                                        putString("user_id", userId)
                                        putString("email", userEmail)
                                        putString("admin_id", adminId)
                                        putString("prices", prices)
                                        putString("firebase_token", token)
                                        apply()
                                    }
                                    Log.d("LoginUser", "Ceny z users_admin: $prices")

                                    updateFirebaseToken(userId, token)

                                    runOnUiThread {
                                        Toast.makeText(this@LoginActivity, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@LoginActivity, OrderActivity::class.java))
                                        finish()
                                    }
                                } else {
                                    Log.e("FirebaseToken", "Błąd pobierania tokenu", task.exception)
                                    runOnUiThread {
                                        Toast.makeText(this@LoginActivity, "Nie udało się pobrać tokena", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            val errorMsg = json.optString("message", "Nieznany błąd logowania")
                            Log.w("LoginUser", "Błąd logowania: $errorMsg")
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("LoginUser", "Błąd parsowania JSON: ${e.message}", e)
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Błąd JSON: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e("LoginUser", "Nieudana odpowiedź serwera: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Błąd odpowiedzi serwera", Toast.LENGTH_SHORT).show()
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
