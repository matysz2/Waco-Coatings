package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.adapter.OrderItemDetailsAdapter
import com.example.waco.data.OrderItem
import com.example.waco.data.OrdersItem
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderItemActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderItemDetailsAdapter
    private lateinit var apiService: ApiService
    private val orderItems = mutableListOf<OrdersItem>()
    private var orderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_items)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pozycje zamówienia"

        recyclerView = findViewById(R.id.recyclerViewOrderItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrderItemDetailsAdapter(orderItems)
        recyclerView.adapter = adapter

        orderId = intent.getIntExtra("order_id", -1)
        if (orderId == -1) {
            Toast.makeText(this, "Brak ID zamówienia", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.textOrderNumber).text = "Zamówienie #$orderId"

        apiService = ApiClient.getClient().create(ApiService::class.java)
        loadOrderItems(orderId.toString())
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
                val intent = Intent(this, OrdersActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
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
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun loadOrderItems(orderId: String) {
        apiService.getOrderItems(orderId).enqueue(object : Callback<List<OrdersItem>> {
            override fun onResponse(call: Call<List<OrdersItem>>, response: Response<List<OrdersItem>>) {
                if (response.isSuccessful) {
                    orderItems.clear()
                    response.body()?.let {
                        orderItems.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("API", "Błąd odpowiedzi: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<OrdersItem>>, t: Throwable) {
                Log.e("API", "Błąd połączenia: ${t.message}")
                Toast.makeText(this@OrderItemActivity, "Błąd połączenia z serwerem", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
