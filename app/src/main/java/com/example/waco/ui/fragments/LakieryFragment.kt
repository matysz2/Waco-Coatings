package com.example.waco.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.ProductAdapter
import com.example.waco.components.ProductDetailActivity
import com.example.waco.data.Product2
import com.example.waco.network.ApiService
import com.example.waco.network.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LakieryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.podklady_fragment, container, false)

        // Inicjalizacja RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerPodklady)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Inicjalizacja Retrofit i ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Pobranie danych
        fetchLakiery()

        return rootView
    }

    private fun fetchLakiery() {
        apiService.getLakiery().enqueue(object : Callback<List<Product2>> {
            override fun onResponse(call: Call<List<Product2>>, response: Response<List<Product2>>) {
                if (response.isSuccessful) {
                    val products = response.body()
                    if (!products.isNullOrEmpty()) {
                        productAdapter = ProductAdapter(products) { product ->
                            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                            intent.putExtra("PRODUCT_CODE", product.kod)
                            startActivity(intent)
                        }
                        recyclerView.adapter = productAdapter
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Błąd pobierania danych: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Product2>>, t: Throwable) {
                Toast.makeText(requireContext(), "Błąd połączenia: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("LakieryFragment", "Błąd połączenia: ${t.message}")
            }
        })
    }

}
