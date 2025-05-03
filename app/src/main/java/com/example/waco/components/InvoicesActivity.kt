package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.adapter.InvoiceAdapter
import com.example.waco.data.Invoice
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvoicesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var invoiceAdapter: InvoiceAdapter
    private lateinit var apiService: ApiService
    private val invoiceList = mutableListOf<Invoice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoices)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "LISTA FAKTUR"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewInvoices)
        recyclerView.layoutManager = LinearLayoutManager(this)
        invoiceAdapter = InvoiceAdapter(invoiceList) { invoice ->
            invoice.link?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Brak pliku do pobrania", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = invoiceAdapter

        apiService = ApiClient.getClient().create(ApiService::class.java)

        loadInvoices()
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
                val intent = Intent(this, DashboardActivity::class.java)
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
    private fun loadInvoices() {
        val sharedPref = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

        if (userId == null) {
            Log.e("API Debug", "Brak user_id w SharedPreferences")
            Toast.makeText(this, "Brak ID użytkownika", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getInvoices(userId).enqueue(object : Callback<List<Invoice>> {
            override fun onResponse(call: Call<List<Invoice>>, response: Response<List<Invoice>>) {
                if (response.isSuccessful) {
                    val invoices = response.body()
                    if (invoices != null) {
                        invoiceList.clear()
                        invoiceList.addAll(invoices)
                        invoiceAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("API Debug", "Błąd odpowiedzi: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Invoice>>, t: Throwable) {
                Log.e("API Debug", "Błąd połączenia: ${t.message}")
            }
        })
    }
}
