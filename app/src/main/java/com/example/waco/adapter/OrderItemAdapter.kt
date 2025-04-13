package com.example.waco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.OrderManager
import com.example.waco.data.Product

class OrderItemAdapter(private var items: MutableList<Product>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        val removeButton: ImageButton = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val product = items[position]
        holder.nameText.text = product.name
        holder.quantityText.text = "Ilość: ${product.quantity}"

        holder.removeButton.setOnClickListener {
            OrderManager.removeProductAt(position) // Usuń produkt z OrderManager
            items.removeAt(position)  // Usuń produkt z listy w adapterze
            notifyItemRemoved(position) // Powiadom adapter o usunięciu
            notifyItemRangeChanged(position, items.size) // Zaktualizuj pozostałe elementy
        }
    }

    override fun getItemCount(): Int = items.size

    // Metoda do zaktualizowania danych w adapterze
    fun updateData(newItems: List<Product>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
