package com.example.waco.components

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var invoiceNumberTextView: TextView
    private lateinit var invoiceAmountTextView: TextView
    private lateinit var downloadInvoiceButton: Button

    private lateinit var orderNumberTextView: TextView
    private lateinit var orderAmountTextView: TextView
    private lateinit var orderPriceTextView: TextView

    private lateinit var accountNameTextView: TextView
    private lateinit var accountEmailTextView: TextView

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "WACO COATINGS"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        apiService = ApiClient.getClient().create(ApiService::class.java)

        invoiceNumberTextView = findViewById(R.id.invoiceNumberTextView)
        invoiceAmountTextView = findViewById(R.id.invoiceAmountTextView)
        downloadInvoiceButton = findViewById(R.id.downloadInvoiceButton)

        orderNumberTextView = findViewById(R.id.orderNumberTextView)
        orderAmountTextView = findViewById(R.id.orderAmountTextView)
        orderPriceTextView = findViewById(R.id.orderPriceTextView)

        accountNameTextView = findViewById(R.id.accountNameTextView)
        accountEmailTextView = findViewById(R.id.accountEmailTextView)

        bottomNav = findViewById(R.id.bottomNavigationView)

        downloadInvoiceButton.setOnClickListener {
            Toast.makeText(this, "Pobieranie faktury (TODO)", Toast.LENGTH_SHORT).show()
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_invoices -> {
                    Toast.makeText(this, "Faktury", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_orders -> {
                    Toast.makeText(this, "Zamówienia", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_pricing -> {
                    Toast.makeText(this, "Cennik", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_account -> {
                    Toast.makeText(this, "Konto", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        loadLatestInvoice()
        loadLatestOrder()
        loadAccountInfo()
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Wylogowanie")
            .setMessage("Czy na pewno chcesz się wylogować?")
            .setPositiveButton("Tak") { _, _ -> logoutAdmin() }
            .setNegativeButton("Nie", null)
            .show()
    }

    private fun logoutAdmin() {
        val prefs = getSharedPreferences("admin_data", MODE_PRIVATE)
        prefs.edit().clear().apply()

        Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginAdminActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun loadLatestInvoice() {
        apiService.getLatestInvoice().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = JSONObject(response.body()?.string() ?: "{}")
                val numer = json.optString("invoice_number", "-")
                val amount = json.optString("amount", "-")
                invoiceNumberTextView.text = "Numer faktury: $numer"
                invoiceAmountTextView.text = "Kwota: $amount zł"
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                invoiceNumberTextView.text = "Błąd ładowania faktury"
            }
        })
    }

    private fun loadLatestOrder() {
        apiService.getLatestOrder().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = JSONObject(response.body()?.string() ?: "{}")
                val numer = json.optString("numer", "-")
                val kwota = json.optString("kwota", "-")
                val cena = json.optString("cena", "-")
                orderNumberTextView.text = "Numer zamówienia: $numer"
                orderAmountTextView.text = "Kwota: $kwota zł"
                orderPriceTextView.text = "Cena: $cena zł"
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                orderNumberTextView.text = "Błąd ładowania zamówienia"
            }
        })
    }

    private fun loadAccountInfo() {
        val sharedPref = getSharedPreferences("admin_data", MODE_PRIVATE)
        accountNameTextView.text = "Nazwa konta: ${sharedPref.getString("name", "Admin")}"
        accountEmailTextView.text = "Email: ${sharedPref.getString("email", "admin@example.com")}"
    }
}
