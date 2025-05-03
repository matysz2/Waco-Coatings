package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
    private lateinit var invoiceDateTextView: TextView
    private lateinit var downloadInvoiceButton: Button
    private lateinit var orderNumberTextView: TextView
    private lateinit var orderAmountTextView: TextView
    private lateinit var orderPriceTextView: TextView
    private lateinit var orderDateTextView: TextView
    private lateinit var accountNameTextView: TextView
    private lateinit var accountEmailTextView: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var drawerLayout: DrawerLayout
    private var invoiceDownloadUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "WACO COATINGS"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)

        invoiceNumberTextView = findViewById(R.id.invoiceNumberTextView)
        invoiceAmountTextView = findViewById(R.id.invoiceAmountTextView)
        invoiceDateTextView = findViewById(R.id.invoiceDateTextView)
        downloadInvoiceButton = findViewById(R.id.downloadInvoiceButton)

        orderNumberTextView = findViewById(R.id.orderNumberTextView)
        orderAmountTextView = findViewById(R.id.orderAmountTextView)
        orderPriceTextView = findViewById(R.id.orderPriceTextView)
        orderDateTextView = findViewById(R.id.orderDateTextView)

        accountNameTextView = findViewById(R.id.accountNameTextView)
        accountEmailTextView = findViewById(R.id.accountEmailTextView)

        bottomNav = findViewById(R.id.bottomNavigationView)
        apiService = ApiClient.getClient().create(ApiService::class.java)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_invoices -> {
                    val intent = Intent(this, InvoicesActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_orders -> {
                    Toast.makeText(this, "Zamówienia", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_pricing -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_account -> {
                    Toast.makeText(this, "Konto", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        downloadInvoiceButton.setOnClickListener {
            invoiceDownloadUrl?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = android.net.Uri.parse(url)
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Brak pliku do pobrania", Toast.LENGTH_SHORT).show()
            }
        }

        loadDashboardData()
        loadAccountInfo()
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                showLogoutDialog()
                true
            }
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Wylogowanie")
            .setMessage("Czy na pewno chcesz się wylogować?")
            .setPositiveButton("Tak") { _, _ ->
                val sharedPref = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
                sharedPref.edit().clear().apply()
                val intent = Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun loadDashboardData() {
        val sharedPref = getSharedPreferences("admin_data", MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

        if (userId == null) {
            Log.e("API Debug", "Brak user_id w SharedPreferences")
            Toast.makeText(this, "Brak ID użytkownika", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getDashboardData(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    val json = JSONObject(body)

                    val invoice = json.optJSONObject("invoice")
                    val invoiceNumber = invoice?.optString("invoice_number", "-")
                    val invoiceAmount = invoice?.optString("amount", "-")
                    val invoiceDate = invoice?.optString("date", "-")
                    invoiceDownloadUrl = invoice?.optString("file", null)

                    invoiceNumberTextView.text = "Numer faktury: $invoiceNumber"
                    invoiceAmountTextView.text = "Kwota: $invoiceAmount zł"
                    invoiceDateTextView.text = "Data: $invoiceDate"

                    val order = json.optJSONObject("order")
                    val orderId = order?.optString("id", "-")
                    val orderStatus = order?.optString("status", "-")
                    val orderPrice = order?.optString("price", "-")
                    val orderDate = order?.optString("created_at", "-")

                    orderNumberTextView.text = "Numer zamówienia: $orderId"
                    orderAmountTextView.text = "Status: $orderStatus"
                    orderPriceTextView.text = "Cena: $orderPrice zł"
                    orderDateTextView.text = "Data: $orderDate"
                } else {
                    Log.e("API Debug", "Błąd odpowiedzi: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API Debug", "Błąd połączenia: ${t.message}")
            }
        })
    }

    private fun loadAccountInfo() {
        val sharedPref = getSharedPreferences("admin_data", MODE_PRIVATE)
        accountNameTextView.text = "Nazwa konta: ${sharedPref.getString("name", "-")}"
        accountEmailTextView.text = "Email: ${sharedPref.getString("email", "-")}"
    }
}
