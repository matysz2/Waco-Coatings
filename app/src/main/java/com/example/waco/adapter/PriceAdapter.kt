package com.example.waco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.PriceItem

class PriceAdapter(private val priceType: String) :
    RecyclerView.Adapter<PriceAdapter.ViewHolder>(), Filterable {

    private var fullList = listOf<PriceItem>()
    private var filteredList = listOf<PriceItem>()

    fun setItems(items: List<PriceItem>) {
        fullList = items
        filteredList = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val kod: TextView = view.findViewById(R.id.kodText)
        val price: TextView = view.findViewById(R.id.priceText)
        val jm: TextView = view.findViewById(R.id.jmText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_price, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        holder.kod.text = item.kod

        val netto = item.price.toDoubleOrNull() ?: 0.0
        val brutto = netto * 1.23

        val formattedNetto = String.format("%.2f", netto)
        val formattedBrutto = String.format("%.2f", brutto)

        val jm = item.jm

        holder.price.text = "Netto: $formattedNetto PLN / $jm\nBrutto: $formattedBrutto PLN / $jm"

        // Opcjonalnie: schowaj osobny jmText
        holder.jm.visibility = View.GONE
    }




    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val result = if (query.isNullOrBlank()) {
                    fullList
                } else {
                    fullList.filter { it.kod.contains(query, ignoreCase = true) }
                }

                return FilterResults().apply { values = result }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<PriceItem>
                notifyDataSetChanged()
            }
        }
    }
}
