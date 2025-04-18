package com.example.waco.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.OrderHistoryAdapter
import com.example.waco.components.OrderDetailsActivity
import com.example.waco.data.Order
import com.example.waco.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderHistoryAdapter
    private var orders = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerView = view.findViewById(R.id.historyRecyclerView)
        adapter = OrderHistoryAdapter(orders) { order ->
            val intent = Intent(activity, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", order.orderId)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val userId = getUserIdFromMemory()
        if (userId != -1) {
            fetchOrders(userId)
        } else {
            Toast.makeText(context, "Błąd: brak użytkownika", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchOrders(userId: Int) {
        val service = RetrofitInstance.create()
        val call = service.getOrders(userId)

        call.enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val fetchedOrders = response.body()
                    if (fetchedOrders.isNullOrEmpty()) {
                        Toast.makeText(context, "Brak zamówień", Toast.LENGTH_SHORT).show()
                    } else {
                        orders.clear()
                        orders.addAll(fetchedOrders)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("HistoryFragment", "Błąd pobierania danych. Kod odpowiedzi: ${response.code()}")
                    Toast.makeText(context, "Błąd pobierania danych", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Log.e("HistoryFragment", "Błąd połączenia: ${t.message}")
                Toast.makeText(context, "Błąd połączenia: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserIdFromMemory(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "") ?: ""
        return if (userId.isNotEmpty()) userId.toInt() else -1
    }
}
