package com.example.waco.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.ColorRgbItem

class ColorRGBAdapter(
    private var colorList: List<ColorRgbItem>,
    private val onItemClick: (ColorRgbItem) -> Unit
) : RecyclerView.Adapter<ColorRGBAdapter.ColorViewHolder>() {

    class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.colorNameTextView)
        val colorView: View = view.findViewById(R.id.colorView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color_rgb, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colorList[position]
        holder.nameTextView.text = color.name
        holder.colorView.setBackgroundColor(Color.rgb(color.r, color.g, color.b))
        holder.itemView.setOnClickListener {
            onItemClick(color)
        }
    }

    override fun getItemCount() = colorList.size

    fun updateData(newList: List<ColorRgbItem>) {
        colorList = newList
        notifyDataSetChanged()
    }
}
