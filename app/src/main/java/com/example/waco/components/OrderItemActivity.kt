package com.example.waco.components

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.OrderItemAdapter
import com.example.waco.data.OrderItem
import com.example.waco.data.Product
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderItemAdapter
    private lateinit var apiService: ApiService
    private val productList = mutableListOf<Product>() // używamy Product, nie OrderItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_items)

        recyclerView = findViewById(R.id.recyclerViewOrderItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrderItemAdapter(productList)
        recyclerView.adapter = adapter

        val orderId = intent.getStringExtra("order_id")
        if (orderId != null) {
            apiService = ApiClient.getClient().create(ApiService::class.java)
            loadOrderItems(orderId)
        } else {
            Toast.makeText(this, "Brak ID zamówienia", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadOrderItems(orderId: String) {
        apiService.getOrderItems(orderId).enqueue(object : Callback<List<OrderItem>> {
            override fun onResponse(
                call: Call<List<OrderItem>>,
                response: Response<List<OrderItem>>
            ) {
                if (response.isSuccessful) {
                    productList.clear()
                    response.body()?.let { orderItems ->
                        val convertedProducts = orderItems.map {
                            Product(
                                id = 0, // albo wyciągnij jeśli masz `it.id`
                                name = it.productName,
                                quantity = it.quantity.toDouble(),
                                price = it.price
                            )
                        }
                        productList.addAll(convertedProducts)
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