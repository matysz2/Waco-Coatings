package com.example.waco.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.OrderItemAdapter
import com.example.waco.data.OrderManager
import com.example.waco.data.OrderRequest
import com.example.waco.data.ProductItem
import com.example.waco.network.RetrofitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentOrderFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var submitButton: Button
    private lateinit var commentEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current_order, container, false)

        submitButton = view.findViewById(R.id.submitOrderButton)
        recyclerView = view.findViewById(R.id.orderRecyclerView)
        commentEditText = view.findViewById(R.id.editTextComment)

        orderAdapter = OrderItemAdapter(OrderManager.getCurrentOrder().toMutableList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = orderAdapter

        submitButton.setOnClickListener {
            animateButton(submitButton)
            submitOrder() // Od razu składamy zamówienie
        }

        return view
    }

    private fun animateButton(button: Button) {
        button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(200).withEndAction {
            button.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
        }
    }

    private fun submitOrder() {
        val comment = commentEditText.text.toString().trim()

        val orderRequest = OrderRequest(
            userId = "", // Usuwamy dane użytkownika
            email = "",  // Usuwamy dane użytkownika
            comment = comment,
            products = OrderManager.getCurrentOrder().map {
                ProductItem(product_name = it.name, quantity = it.quantity)
            }
        )

        // Generowanie podsumowania zamówienia
        val orderSummary = generateOrderSummary(orderRequest)

        showOrderSummaryDialog(orderSummary) {
            sendOrder(orderRequest)
        }
    }

    private fun generateOrderSummary(orderRequest: OrderRequest): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Komentarz: ${orderRequest.comment}\n\n")
        stringBuilder.append("Produkty:\n")
        // Zmiana z 'items' na 'products'
        orderRequest.products.forEach { item ->
            stringBuilder.append("- ${item.product_name} x${item.quantity}\n")
        }
        return stringBuilder.toString()
    }

    private fun showOrderSummaryDialog(orderSummary: String, onConfirm: () -> Unit) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Podsumowanie zamówienia")
        builder.setMessage(orderSummary)
        builder.setPositiveButton("Złóż zamówienie") { _, _ -> onConfirm() }
        builder.setNegativeButton("Anuluj", null)
        builder.show()
    }

    private fun sendOrder(orderRequest: OrderRequest) {
        RetrofitInstance.api.submitOrder(orderRequest)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Po złożeniu zamówienia, wyświetlamy komunikat
                        showOrderStatusDialog("Zamówienie złożone!", R.drawable.success_image)
                        // Czyścimy zamówienie po sukcesie
                        OrderManager.clear()
                        orderAdapter.updateOrder(OrderManager.getCurrentOrder().toMutableList())
                    } else {
                        showOrderStatusDialog("Błąd podczas składania zamówienia", R.drawable.error_image)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showOrderStatusDialog("Błąd sieciowy", R.drawable.error_image)
                }
            })
    }

    private fun showOrderStatusDialog(message: String, imageResId: Int) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Status zamówienia")
        builder.setMessage(message)
        builder.setIcon(imageResId) // Dodajemy obrazek do dialogu
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        orderAdapter.updateOrder(OrderManager.getCurrentOrder().toMutableList())
    }
}
