package com.example.waco.components


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.OrdersAdapterAdmin
import com.example.waco.data.Order
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrdersAdapterAdmin
    private val orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders)

        recyclerView = findViewById(R.id.recyclerOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrdersAdapterAdmin(orders)
        recyclerView.adapter = adapter

        fetchOrders()
    }

    private fun fetchOrders() {
        val api = ApiClient.getClient().create(ApiService::class.java)
        api.getAllOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful && response.body() != null) {
                    orders.clear()
                    orders.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AllOrdersActivity, "Nie udało się pobrać zamówień", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@AllOrdersActivity, "Błąd: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
