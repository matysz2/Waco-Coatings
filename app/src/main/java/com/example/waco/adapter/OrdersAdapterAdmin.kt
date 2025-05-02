package com.example.waco.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Order

class OrdersAdapterAdmin(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapterAdmin.OrderAdminViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_admin, parent, false)
        return OrderAdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderAdminViewHolder, position: Int) {
        val order = orders[position]
        holder.textId.text = "ID: ${order.orderId}"
        holder.textUser.text = "Użytkownik: ${order.userId}"
        holder.textDate.text = "Data: ${order.orderDate}"
        holder.textStatus.text = "Status: ${order.status}"
        holder.textComment.text = "Komentarz: ${if (order.comment.isNotBlank()) order.comment else "Brak"}"
    }

    override fun getItemCount(): Int = orders.size

    class OrderAdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textId: TextView = view.findViewById(R.id.textOrderAdminId)
        val textUser: TextView = view.findViewById(R.id.textOrderAdminUser)
        val textDate: TextView = view.findViewById(R.id.textOrderAdminDate)
        val textStatus: TextView = view.findViewById(R.id.textOrderAdminStatus)
        val textComment: TextView = view.findViewById(R.id.textOrderAdminComment)
    }
}
