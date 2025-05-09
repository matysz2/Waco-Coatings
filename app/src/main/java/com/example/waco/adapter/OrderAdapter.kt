package com.example.waco.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.components.OrderItemActivity
import com.example.waco.data.Order

class OrderAdapter(
    private val orderList: List<Order>,
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewOrderId: TextView = itemView.findViewById(R.id.textViewOrderId)
        val textViewOrderStatus: TextView = itemView.findViewById(R.id.textViewOrderStatus)
        val textViewOrderPrice: TextView = itemView.findViewById(R.id.textViewOrderPrice)
        val textViewOrderCreatedAt: TextView = itemView.findViewById(R.id.textViewOrderCreatedAt)
        val buttonDetails: Button = itemView.findViewById(R.id.buttonOrderDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_orders, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int = orderList.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.textViewOrderId.text = "Numer: ${order.orderId}"
        holder.textViewOrderStatus.text = "Status: ${order.status}"
        holder.textViewOrderPrice.text = "Kwota: ${order.price} zł"
        holder.textViewOrderCreatedAt.text = "Utworzono: ${order.createdAt}"

        holder.buttonDetails.setOnClickListener {
            val context: Context = holder.itemView.context
            val intent = Intent(context, OrderItemActivity::class.java)
            intent.putExtra("order_id", order.orderId)
            context.startActivity(intent)
        }
    }
}
