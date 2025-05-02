package com.example.waco.components

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Invoice
import com.example.waco.network.ApiClient
import com.example.waco.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllInvoicesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InvoiceAdapter
    private val invoices = mutableListOf<Invoice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_invoices)

        recyclerView = findViewById(R.id.recyclerOrders) // Zmieniono na recyclerOrders
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = InvoiceAdapter(invoices)
        recyclerView.adapter = adapter

        fetchInvoices()
    }

    private fun fetchInvoices() {
        val api = ApiClient.getClient().create(ApiService::class.java)
        api.getAllInvoices().enqueue(object : Callback<List<Invoice>> {
            override fun onResponse(call: Call<List<Invoice>>, response: Response<List<Invoice>>) {
                if (response.isSuccessful && response.body() != null) {
                    invoices.clear()
                    invoices.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AllInvoicesActivity, "Nie udało się pobrać faktur", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Invoice>>, t: Throwable) {
                Toast.makeText(this@AllInvoicesActivity, "Błąd: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
