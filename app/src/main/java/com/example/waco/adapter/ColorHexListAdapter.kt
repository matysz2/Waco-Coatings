package com.example.waco.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.ColorHex

class ColorHexListAdapter(
    private var colorList: List<ColorHex>,
    private val onItemClick: (ColorHex) -> Unit
) : RecyclerView.Adapter<ColorHexListAdapter.ColorViewHolder>() {

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorName: TextView = itemView.findViewById(R.id.colorName)
        val colorPreview: View = itemView.findViewById(R.id.colorPreview)

        init {
            itemView.setOnClickListener {
                onItemClick(colorList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_hex, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val item = colorList[position]
        holder.colorName.text = item.name
        holder.colorPreview.setBackgroundColor(android.graphics.Color.parseColor(item.hex))
    }


    override fun getItemCount() = colorList.size

    fun updateData(newList: List<ColorHex>) {
        colorList = newList
        notifyDataSetChanged()
    }
}
