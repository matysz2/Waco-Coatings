package com.example.waco.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.ProductAdapter
import com.example.waco.adapter.ProductAdapter2
import com.example.waco.data.OrderManager
import com.example.waco.data.Product
import com.example.waco.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Response


class AddProductFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var productList: RecyclerView
    private lateinit var adapter: ProductAdapter2
    private var allProducts = listOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        searchView = view.findViewById(R.id.searchView)
        productList = view.findViewById(R.id.recyclerView)

        adapter = ProductAdapter2 { product ->
            // Po kliknięciu w "Dodaj" – dodaj do wspólnej listy (np. singleton lub ViewModel)
            OrderManager.addProduct(product)
        }

        productList.layoutManager = LinearLayoutManager(requireContext())
        productList.adapter = adapter

        fetchProducts()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        return view
    }

    private fun fetchProducts() {
        RetrofitInstance.api.getProducts().enqueue(object : retrofit2.Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    allProducts = response.body() ?: emptyList()
                    adapter.setData(allProducts)
                } else {
                    Toast.makeText(requireContext(), "Nie udało się pobrać danych", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(requireContext(), "Błąd: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
