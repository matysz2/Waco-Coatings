// OrderHistoryAdapter.kt
package com.example.waco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Order

class OrderHistoryAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdText: TextView = itemView.findViewById(R.id.productNameText)  // zamień na coś sensownego
        val statusText: TextView = itemView.findViewById(R.id.quantityText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderIdText.text = "Zamówienie #${order.orderId}"
        holder.statusText.text = "Status: ${order.status}"
        holder.dateText.text = "Data: ${order.orderDate}"
    }

    override fun getItemCount(): Int = orders.size
}
