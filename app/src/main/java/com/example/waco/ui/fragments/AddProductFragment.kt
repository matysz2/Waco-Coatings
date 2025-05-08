package com.example.waco.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.ProductAdapter2
import com.example.waco.data.OrderManager
import com.example.waco.data.Product
import com.example.waco.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var productList: RecyclerView
    private lateinit var adapter: ProductAdapter2
    private lateinit var btnAddProduct: View
    private var allProducts = listOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        searchView = view.findViewById(R.id.searchView)
        productList = view.findViewById(R.id.recyclerView)
        btnAddProduct = view.findViewById(R.id.btnAddProduct)

        adapter = ProductAdapter2 { product ->
            showQuantityDialog(product)
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
        btnAddProduct.setOnClickListener {
            animateButton(btnAddProduct) // uruchom animację

            val query = searchView.query.toString().trim()
            if (query.isNotEmpty()) {
                val matched = allProducts.find { it.name.equals(query, ignoreCase = true) }
                val product = matched ?: Product(id = -1, name = query, quantity = 0.0, price = 0.0)
                showQuantityDialog(product)
            } else {
                Toast.makeText(requireContext(), "Wpisz nazwę produktu", Toast.LENGTH_SHORT).show()
            }
        }



        return view
    }

    private fun fetchProducts() {
        RetrofitInstance.api.getProducts().enqueue(object : Callback<List<Product>> {
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

    private fun showQuantityDialog(product: Product) {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Podaj ilość"
        }



        AlertDialog.Builder(requireContext())
            .setTitle("Dodaj produkt")
            .setMessage("Produkt: ${product.name}")
            .setView(input)
            .setPositiveButton("Dodaj") { _, _ ->
                val quantity = input.text.toString()
                if (quantity.isNotEmpty()) {
                    val modifiedProduct = product.copy(quantity = quantity.toDoubleOrNull() ?: 0.0)
                    OrderManager.addProduct(modifiedProduct)
                    Toast.makeText(requireContext(), "Dodano: ${modifiedProduct.quantity} kg ${modifiedProduct.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Podaj ilość!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }
    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

}
