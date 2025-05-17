package com.example.waco.components
import android.content.pm.PackageManager

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
import android.Manifest

import androidx.core.app.ActivityCompat

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
            invoice.link?.let { rawPath ->

                // Sprawdź, czy rawPath to pełny URL czy ścieżka
                val fullUrl = if (rawPath.startsWith("http://") || rawPath.startsWith("http://")) {
                    rawPath
                } else {
                    // Jeśli to ścieżka względna - dołóż https i usuń zbędne ukośniki z przodu
                    val cleanPath = rawPath.trimStart('/', '\\')
                    "http://$cleanPath"
                }

                Log.d("DownloadTest", "Pobierany URL: $fullUrl")

                val fileName = "faktura_${invoice.invoice_number ?: System.currentTimeMillis()}.pdf"

                // Sprawdzenie uprawnień do zapisu plików (w starszych Androidach)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)

                    Toast.makeText(this, "Brak uprawnień do zapisu plików, proszę zezwolić", Toast.LENGTH_SHORT).show()
                    return@let
                }

                try {
                    val request = DownloadManager.Request(Uri.parse(fullUrl)).apply {
                        setTitle("Pobieranie faktury")
                        setDescription("Trwa pobieranie pliku faktury")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setAllowedOverMetered(true)
                        setAllowedOverRoaming(true)
                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    }

                    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val id = downloadManager.enqueue(request)
                    Log.d("DownloadTest", "DownloadManager enqueued with id: $id")

                    Toast.makeText(this, "Rozpoczęto pobieranie faktury", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(this, "Błąd podczas pobierania: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DownloadError", "Błąd pobierania: ", e)
                }

            } ?: run {
                Toast.makeText(this, "Brak linku do faktury", Toast.LENGTH_SHORT).show()
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
    override fun onBackPressed() {
        super.onBackPressed()
        // Intencjonalnie NIE wywołujemy super.onBackPressed(),
        // bo ręcznie przechodzimy do DashboardActivity
        goToDashboard()
    }
    private fun downloadFile(context: Context, url: String, title: String, fileName: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription("Pobieranie pliku faktury...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Pobieranie rozpoczęte...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Błąd pobierania: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("Download", "Błąd: ${e.message}")
        }
    }


    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
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
