package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.ui.fragments.CurrentOrderFragment
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

        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, OrderActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.login_activity)

        // Ustawienie Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = "LOGOWANIE"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Pokazuje strzałkę powrotu

        fun onSupportNavigateUp(): Boolean {
            // Zamiast używać onBackPressed, przekierowujemy do konkretnej aktywności
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)  // Uruchamiamy MainActivity
            finish()  // Zakończ obecna aktywność, aby nie pozostała w stosie
            return true
        }

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Wprowadź dane logowania", Toast.LENGTH_SHORT).show()
            }
        }
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
                            val userEmail = json.optString("email")  // Dodanie emaila do odpowiedzi

                            // Zapisz dane do SharedPreferences
                            val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                            sharedPref.edit().apply {
                                putBoolean("is_logged_in", true)
                                putString("user_id", userId)
                                putString("email", userEmail)  // Zapisujemy email
                                apply()
                            }

                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, OrderActivity::class.java))
                                finish()
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

    override fun onBackPressed() {
        // Tworzenie okna dialogowego potwierdzenia
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { dialog, id ->
                // Wywołanie standardowej akcji wyjścia (kończy aktywność)
                super.onBackPressed()
            }
            .setNegativeButton("Nie") { dialog, id ->
                dialog.dismiss()  // Anulowanie zamknięcia aplikacji
            }

        // Wyświetlenie okna dialogowego
        val alert = builder.create()
        alert.show()
    }
}
