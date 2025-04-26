package com.example.waco.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.ColorItem

class ColorAdapter(
    private var originalList: List<ColorItem>,
    private val onItemClick: (ColorItem) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>(), Filterable {

    private var filteredList: List<ColorItem> = originalList

    inner class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorName: TextView = view.findViewById(R.id.colorName)
        val colorSquare: ImageView = view.findViewById(R.id.colorSquare)

        init {
            view.setOnClickListener {
                onItemClick(filteredList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorItem = filteredList[position]
        holder.colorName.text = colorItem.name
        try {
            holder.colorSquare.setBackgroundColor(Color.parseColor(colorItem.hex))
        } catch (e: IllegalArgumentException) {
            holder.colorSquare.setBackgroundColor(Color.GRAY)
        }
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase() ?: ""
                val results = if (query.isEmpty()) {
                    originalList
                } else {
                    originalList.filter { it.name.lowercase().contains(query) }
                }
                return FilterResults().apply { values = results }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<ColorItem> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    fun updateData(newList: List<ColorItem>) {
        originalList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}
