package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.model.AccountUpdateRequest
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var invoiceNumberTextView: TextView
    private lateinit var invoiceAmountTextView: TextView
    private lateinit var invoiceDateTextView: TextView
    private lateinit var invoiceGrossTextView: TextView
    private lateinit var downloadInvoiceButton: Button

    private lateinit var orderNumberTextView: TextView
    private lateinit var orderAmountTextView: TextView
    private lateinit var orderPriceTextView: TextView
    private lateinit var orderGrossTextView: TextView
    private lateinit var orderDateTextView: TextView

    private lateinit var accountNameTextView: TextView
    private lateinit var accountEmailTextView: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var apiService: ApiService
    private lateinit var drawerLayout: DrawerLayout
    private var invoiceDownloadUrl: String? = null

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveButton: Button

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
        invoiceGrossTextView = findViewById(R.id.invoiceGrossTextView)
        downloadInvoiceButton = findViewById(R.id.downloadInvoiceButton)

        orderNumberTextView = findViewById(R.id.orderNumberTextView)
        orderAmountTextView = findViewById(R.id.orderAmountTextView)
        orderPriceTextView = findViewById(R.id.orderPriceTextView)
        orderGrossTextView = findViewById(R.id.orderGrossTextView)
        orderDateTextView = findViewById(R.id.orderDateTextView)

        val lastInvoiceCard = findViewById<CardView>(R.id.lastInvoiceCard)
        val lastOrderCard = findViewById<CardView>(R.id.lastOrderCard)
        val accountInfoCard: View = findViewById(R.id.accountInfoCard)

        accountNameTextView = findViewById(R.id.accountNameTextView)
        accountEmailTextView = findViewById(R.id.accountEmailTextView)

        bottomNav = findViewById(R.id.bottomNavigationView)
        apiService = ApiClient.getClient().create(ApiService::class.java)

        nameEditText = findViewById(R.id.editTextName)
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        saveButton = findViewById(R.id.saveAccountButton)

        nameEditText.visibility = View.GONE
        emailEditText.visibility = View.GONE
        passwordEditText.visibility = View.GONE
        saveButton.visibility = View.GONE


        val navView = findViewById<NavigationView>(R.id.nav_view)

        navView.setNavigationItemSelectedListener { menuItem ->
            val groupName = menuItem.title.toString() // np. "Kolory RAL Classic K7", "Podkłady"

            val sharedPref = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
            val priceType = sharedPref.getString("price", "hurtowa1") ?: "hurtowa1"

            val intent = Intent(this, PriceListActivity::class.java).apply {
                putExtra("group", groupName)
                putExtra("priceType", priceType)
            }

            startActivity(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }




        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val userId = getSharedPreferences("admin_data", MODE_PRIVATE).getString("user_id", null)

            if (userId == null) {
                Toast.makeText(this, "Brak ID użytkownika", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AccountUpdateRequest(userId, name, email, password)
            apiService.updateAccountData(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DashboardActivity, "Dane zapisane", Toast.LENGTH_SHORT).show()
                        val sharedPref = getSharedPreferences("admin_data", MODE_PRIVATE)
                        sharedPref.edit()
                            .putString("user_id", userId)
                            .putString("name", name)
                            .putString("email", email)
                            .putString("password", password)
                            .apply()
                    } else {
                        Toast.makeText(this@DashboardActivity, "Błąd podczas zapisu", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@DashboardActivity, "Błąd połączenia: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_invoices -> {
                    startActivity(Intent(this, InvoicesActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_orders -> {
                    startActivity(Intent(this, OrdersActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_pricing -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }
                R.id.navigation_account -> {
                    nameEditText.visibility = View.VISIBLE
                    emailEditText.visibility = View.VISIBLE
                    passwordEditText.visibility = View.VISIBLE
                    saveButton.visibility = View.VISIBLE

                    invoiceNumberTextView.visibility = View.GONE
                    invoiceAmountTextView.visibility = View.GONE
                    invoiceDateTextView.visibility = View.GONE
                    invoiceGrossTextView.visibility = View.GONE
                    downloadInvoiceButton.visibility = View.GONE

                    orderNumberTextView.visibility = View.GONE
                    orderAmountTextView.visibility = View.GONE
                    orderPriceTextView.visibility = View.GONE
                    orderGrossTextView.visibility = View.GONE
                    orderDateTextView.visibility = View.GONE

                    lastInvoiceCard.visibility = View.GONE
                    lastOrderCard.visibility = View.GONE
                    accountInfoCard.visibility = View.GONE

                    val sharedPref = getSharedPreferences("admin_data", MODE_PRIVATE)
                    nameEditText.setText(sharedPref.getString("name", ""))
                    emailEditText.setText(sharedPref.getString("email", ""))
                    passwordEditText.setText(sharedPref.getString("password", ""))

                    true
                }
                else -> false
            }
        }

        downloadInvoiceButton.setOnClickListener {
            invoiceDownloadUrl?.let { url ->
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = android.net.Uri.parse(url)
                })
            } ?: Toast.makeText(this, "Brak pliku do pobrania", Toast.LENGTH_SHORT).show()
        }

        loadDashboardData()
        loadAccountInfo()
    }

    private fun loadDashboardData() {
        val userId = getSharedPreferences("admin_data", MODE_PRIVATE).getString("user_id", null)

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
                    val invoiceAmount = invoice?.optString("amount", "0") ?: "0"
                    val invoiceGross = (invoiceAmount.toDoubleOrNull() ?: 0.0) * 1.23

                    invoiceNumberTextView.text = "Numer faktury: ${invoice?.optString("invoice_number", "-")}"
                    invoiceAmountTextView.text = "Kwota netto: $invoiceAmount zł"
                    invoiceGrossTextView.text = "Kwota z VAT: %.2f zł".format(invoiceGross)

                    // Pobranie i formatowanie daty faktury
                    val invoiceDateRaw = invoice?.optString("created_at") ?: "-"
                    val invoiceDateFormatted = invoiceDateRaw.replace("\"", "-")
                    invoiceDateTextView.text = "Data: $invoiceDateFormatted"

                    invoiceDownloadUrl = invoice?.optString("file", null)

                    val order = json.optJSONObject("order")
                    val orderPrice = order?.optString("price", "0") ?: "0"
                    val orderGross = (orderPrice.toDoubleOrNull() ?: 0.0) * 1.23

                    orderNumberTextView.text = "Numer zamówienia: ${order?.optString("id", "-")}"
                    orderAmountTextView.text = "Status: ${order?.optString("status", "-")}"
                    orderPriceTextView.text = "Kwota netto: $orderPrice zł"
                    orderGrossTextView.text = "Kwota z VAT: %.2f zł".format(orderGross)

                    // Pobranie i formatowanie daty zamówienia
                    val orderDateRaw = order?.optString("created_at") ?: "-"
                    val orderDateFormatted = orderDateRaw.replace("\"", "-")
                    orderDateTextView.text = "Data: $orderDateFormatted"
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Zamknij menu cennika, jeśli otwarte
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (isAccountEditVisible()) {
            // Jeśli jesteśmy na ekranie edycji konta (pola widoczne), wróć do Dashboard (lub MainActivity)
            goToDashboardActivity()
        } else {
            // Standardowe zachowanie – cofnięcie lub zamknięcie activity
            super.onBackPressed()
        }
    }

    // Funkcja pomocnicza sprawdzająca, czy widoki edycji konta są widoczne
    private fun isAccountEditVisible(): Boolean {
        return nameEditText.visibility == View.VISIBLE &&
                emailEditText.visibility == View.VISIBLE &&
                passwordEditText.visibility == View.VISIBLE &&
                saveButton.visibility == View.VISIBLE
    }


    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
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
                // Po kliknięciu strzałki cofamy się do MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToDashboardActivity() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Wylogowanie")
            .setMessage("Czy na pewno chcesz się wylogować?")
            .setPositiveButton("Tak") { _, _ ->
                val sharedPref = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
                sharedPref.edit().clear().apply()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }
}
