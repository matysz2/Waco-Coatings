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
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginAdminActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)
        val userEmail = sharedPref.getString("email", null)
        val firebaseToken = sharedPref.getString("firebase_token", null)

        if (!userId.isNullOrEmpty() && !userEmail.isNullOrEmpty() && !firebaseToken.isNullOrEmpty()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.login_admin_activity)
        FirebaseApp.initializeApp(this)

        apiService = ApiClient.getClient().create(ApiService::class.java)

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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    private fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Proszę wprowadzić email i hasło", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("LoginAdmin", "🟢 Rozpoczynanie logowania... email=$email, password=$password")

        apiService.loginUser(email, password).enqueue(object : Callback<okhttp3.ResponseBody> {
            override fun onResponse(
                call: Call<okhttp3.ResponseBody>,
                response: Response<okhttp3.ResponseBody>
            ) {
                Log.d("LoginAdmin", "🟢 Odpowiedź HTTP: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    try {
                        val jsonString = response.body()!!.string()
                        Log.d("LoginAdmin", "🟢 Odpowiedź JSON: $jsonString")

                        val json = JSONObject(jsonString)
                        val status = json.optString("status")

                        if (status == "success") {
                            val userId = json.optString("user_id")
                            val userEmail = json.optString("email")
                            val userName = json.optString("name") // nowy element

                            Log.d("LoginAdmin", "🟢 Użytkownik OK: ID=$userId, Email=$userEmail, Name=$userName")

                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val token = task.result
                                    Log.d("LoginAdmin", "🟢 Firebase token: $token")

                                    val sharedPref = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
                                    sharedPref.edit().apply {
                                        putBoolean("admin_logged_in", true)
                                        putString("user_id", userId)
                                        putString("name", userName)
                                        putString("email", userEmail)
                                        putString("firebase_token", token)
                                        apply()
                                    }

                                    updateFirebaseToken(userId.toInt(), token)

                                    runOnUiThread {
                                        Toast.makeText(this@LoginAdminActivity, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@LoginAdminActivity, DashboardActivity::class.java))
                                        finish()
                                    }
                                } else {
                                    Log.e("LoginAdmin", "🔴 Nie udało się pobrać tokena Firebase")
                                    Toast.makeText(this@LoginAdminActivity, "Nie udało się pobrać tokena", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            val errorMsg = json.optString("message", "Nieznany błąd logowania")
                            Log.e("LoginAdmin", "🔴 Logowanie nieudane: $errorMsg")
                            Toast.makeText(this@LoginAdminActivity, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginAdmin", "🔴 Błąd JSON: ${e.localizedMessage}", e)
                        Toast.makeText(this@LoginAdminActivity, "Błąd JSON: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("LoginAdmin", "🔴 Błąd odpowiedzi serwera: ${response.errorBody()?.string()}")
                    Toast.makeText(this@LoginAdminActivity, "Błąd odpowiedzi serwera", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                Log.e("LoginAdmin", "🔴 Błąd połączenia: ${t.localizedMessage}", t)
                Toast.makeText(this@LoginAdminActivity, "Błąd połączenia: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateFirebaseToken(userId: Int, token: String) {
        Log.d("LoginAdmin", "🟢 Wysyłanie tokena Firebase (userId=$userId): $token")

        apiService.updateFcmToken(userId, token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("LoginAdmin", "🟢 Token Firebase zaktualizowany poprawnie.")
                } else {
                    Log.e("LoginAdmin", "🔴 Błąd przy aktualizacji tokenu: ${response.code()}")
                    Toast.makeText(this@LoginAdminActivity, "Błąd przy aktualizacji tokenu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("LoginAdmin", "🔴 Błąd połączenia z aktualizacją tokenu: ${t.message}", t)
                Toast.makeText(this@LoginAdminActivity, "Błąd połączenia z aktualizacją tokenu", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
