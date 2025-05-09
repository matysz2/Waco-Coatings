package com.example.waco.components

import android.annotation.SuppressLint
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
import com.example.waco.data.OrdersItem
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderItemDetailsAdapter
    private val detailsList = mutableListOf<OrderItem>()
    private lateinit var totalTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_details)

        recyclerView = findViewById(R.id.recyclerViewOrderDetails)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrderItemDetailsAdapter(detailsList)
        recyclerView.adapter = adapter

        totalTextView = findViewById(R.id.textViewTotalPrice)

        var orderId: String? = intent.getStringExtra("order_id")
        if (orderId == null) {
            val orderIdInt = intent.getIntExtra("order_id", -1)
            if (orderIdInt != -1) {
                orderId = orderIdInt.toString()
            } else {
                Toast.makeText(this, "Brak lub nieprawidłowe ID zamówienia", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        }

        loadOrderDetails(orderId)
    }

    private fun loadOrderDetails(orderId: String) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)
        apiService.getOrdersDetails(orderId).enqueue(object : Callback<List<OrdersItem>> {
            override fun onResponse(call: Call<List<OrdersItem>>, response: Response<List<OrdersItem>>) {
                if (response.isSuccessful) {
                    val items = response.body()
                    if (items != null) {
                        detailsList.clear()
                        val converted = items.map {
                            OrderItem(
                                id = it.id,
                                orderId = it.orderId,
                                productName = it.productName,
                                quantity = it.quantity,
                                price = it.price,
                                totalPrice = it.totalPrice
                            )
                        }
                        detailsList.addAll(converted)
                        adapter.notifyDataSetChanged()

                        val total = converted.sumOf { it.totalPrice }
                        totalTextView.text = "Suma: %.2f zł".format(total)
                    }
                } else {
                    Log.e("OrdersDetailsActivity", "Błąd odpowiedzi z serwera: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<OrdersItem>>, t: Throwable) {
                Log.e("OrdersDetailsActivity", "Błąd połączenia: ${t.message}")
                Toast.makeText(this@OrdersDetailsActivity, "Błąd połączenia z serwerem", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
