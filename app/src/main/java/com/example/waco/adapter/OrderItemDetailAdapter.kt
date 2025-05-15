package com.example.waco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.OrdersItem

class OrderItemDetailsAdapter(private val items: List<OrdersItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.textProductName)
        val quantity: TextView = view.findViewById(R.id.textQuantity)
        val price: TextView = view.findViewById(R.id.textPrice)
        val grossPrice: TextView = view.findViewById(R.id.textGrossPrice)
        val totalPrice: TextView = view.findViewById(R.id.textTotalPrice)
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val grandTotal: TextView = view.findViewById(R.id.textGrandTotal)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_detail, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_total_footer, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder && position < items.size) {
            val item = items[position]
            val netPrice = item.price ?: 0.0
            val grossPrice = netPrice * 1.23
            val quantity = item.quantity ?: 0.0
            val totalGross = grossPrice * quantity

            holder.productName.text = item.productName ?: "Brak nazwy"
            holder.quantity.text = "Ilość: %.2f".format(quantity)
            holder.price.text = "Cena netto: %.2f zł".format(netPrice)
            holder.grossPrice.text = "Cena brutto: %.2f zł".format(grossPrice)
            holder.totalPrice.text = "Razem: %.2f zł".format(totalGross)

        } else if (holder is FooterViewHolder) {
            val totalGross = items.sumOf {
                val net = it.price ?: 0.0
                val qty = it.quantity ?: 0.0
                (net * 1.23) * qty
            }
            holder.grandTotal.text = "Razem brutto: %.2f zł".format(totalGross)
        }
    }

    override fun getItemCount(): Int = items.size + 1 // +1 for footer
}
