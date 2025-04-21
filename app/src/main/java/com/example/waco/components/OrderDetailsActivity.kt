package com.example.waco.components

import OrderDetailsAdapter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.OrderDetails
import com.example.waco.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var orderNumberText: TextView
    private lateinit var orderDateText: TextView
    private lateinit var orderStatusText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderDetailsAdapter
    private lateinit var orderCommentText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        val orderId = intent.getIntExtra("orderId", -1)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Szczegóły zamówienia"



        orderNumberText = findViewById(R.id.orderNumberText)
        orderDateText = findViewById(R.id.orderDateText)
        orderStatusText = findViewById(R.id.orderStatusText)
        orderCommentText = findViewById(R.id.orderCommentText) // nowa linia
        recyclerView = findViewById(R.id.productsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        if (orderId != -1) {
            fetchOrderDetails(orderId)
        } else {
            Toast.makeText(this, "Brak ID zamówienia", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    override  fun onSupportNavigateUp(): Boolean {
        finish() // zamyka aktywność i wraca do poprzedniego ekranu (czyli fragmentu)
        return true
    }
    private fun fetchOrderDetails(orderId: Int) {
        val service = RetrofitInstance.create()
        val call = service.getOrderDetails(orderId)

        call.enqueue(object : Callback<OrderDetails> {
            override fun onResponse(call: Call<OrderDetails>, response: Response<OrderDetails>) {
                if (response.isSuccessful) {
                    val details = response.body()
                    if (details != null) {
                        orderNumberText.text = "Numer zamówienia: ${details.orderId}"
                        orderDateText.text = "Data: ${details.orderDate}"
                        orderStatusText.text = "Status: ${details.status}"
                        orderCommentText.text = "Komentarz: ${details.comment}" // nowa linia

                        adapter = OrderDetailsAdapter(details.items)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@OrderDetailsActivity, "Błąd danych", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<OrderDetails>, t: Throwable) {
                Log.e("OrderDetailsActivity", "Błąd: ${t.message}")
                Toast.makeText(this@OrderDetailsActivity, "Błąd połączenia", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}