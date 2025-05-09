package com.example.waco.components

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.OrderItemDetailsAdapter
import com.example.waco.data.OrderItem
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderItemActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderItemDetailsAdapter
    private lateinit var apiService: ApiService
    private val orderItems = mutableListOf<OrderItem>()
    private var orderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_items)

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

    private fun loadOrderItems(orderId: String) {
        apiService.getOrderItems(orderId).enqueue(object : Callback<List<OrderItem>> {
            override fun onResponse(call: Call<List<OrderItem>>, response: Response<List<OrderItem>>) {
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

            override fun onFailure(call: Call<List<OrderItem>>, t: Throwable) {
                Log.e("API", "Błąd połączenia: ${t.message}")
            }
        })
    }
}
