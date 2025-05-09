package com.example.waco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.OrderItem

class OrderItemDetailsAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemDetailsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.textProductName)
        val quantity: TextView = view.findViewById(R.id.textQuantity)
        val price: TextView = view.findViewById(R.id.textPrice)
        val totalPrice: TextView = view.findViewById(R.id.textTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.productName.text = item.productName ?: "Brak nazwy"
        holder.quantity.text = "Ilość: ${item.quantity}"
        holder.price.text = "Cena: ${item.price} zł"
        holder.totalPrice.text = "Razem: ${item.totalPrice} zł"
    }

    override fun getItemCount() = items.size
}
