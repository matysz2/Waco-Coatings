package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.PriceAdapter
import com.example.waco.data.PriceItem
import com.example.waco.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PriceListActivity : AppCompatActivity() {

    private lateinit var adapter: PriceAdapter
    private lateinit var priceType: String
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price_list)

        val group = intent.getStringExtra("group") ?: return
        priceType = intent.getStringExtra("priceType") ?: "hurtowa1"

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        fetchPrices(group)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Cennik"
        }
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PriceAdapter(priceType)
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@PriceListActivity)
            adapter = this@PriceListActivity.adapter
        }
    }

    private fun setupSearchView() {
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun fetchPrices(group: String) {
        val sharedPref = getSharedPreferences("admin_data", Context.MODE_PRIVATE)
        val priceColumn = sharedPref.getString("price", "hurtowa1") ?: "hurtowa1"

        Log.d("PriceFetch", "Fetching prices with group='$group' and column='$priceColumn'")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prices = RetrofitInstance.api.getPrices(group, priceColumn)
                Log.d("PriceFetch", "Received ${prices.size} items from API")
                withContext(Dispatchers.Main) {
                    adapter.setItems(prices)
                }
            } catch (e: Exception) {
                Log.e("PriceFetch", "API Error", e)
            }
        }
    }

}
