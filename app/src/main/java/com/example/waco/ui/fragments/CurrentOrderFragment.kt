package com.example.waco.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.adapter.OrderItemAdapter
import com.example.waco.data.OrderManager
import com.example.waco.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentOrderFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var submitButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_current_order, container, false)

        recyclerView = view.findViewById(R.id.orderRecyclerView)
        submitButton = view.findViewById(R.id.submitOrderButton)

        // Adapter powinien być utworzony z bieżącej listy zamówionych produktów.
        orderAdapter = OrderItemAdapter(OrderManager.getCurrentOrder().toMutableList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = orderAdapter

        submitButton.setOnClickListener {
            RetrofitInstance.api.submitOrder(OrderManager.getCurrentOrder()).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Zamówienie złożone!", Toast.LENGTH_SHORT).show()
                        OrderManager.clear() // Wyczyść zamówienie po złożeniu.
                        orderAdapter.updateOrder(OrderManager.getCurrentOrder().toMutableList()) // Odśwież dane w adapterze.
                    } else {
                        Toast.makeText(requireContext(), "Błąd podczas składania zamówienia", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Błąd: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }

    // Dodajemy metodę onResume, żeby odświeżyć dane, gdy fragment wróci na ekran.
    override fun onResume() {
        super.onResume()
        orderAdapter.updateOrder(OrderManager.getCurrentOrder().toMutableList()) // Aktualizuj dane po powrocie do fragmentu
    }
}
