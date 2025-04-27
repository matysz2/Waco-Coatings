package com.example.waco.adapter

import android.graphics.Color as AndroidColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Color

class ColorListAdapter(
    private var colors: List<Color>,
    private val onColorClick: (Color) -> Unit
) : RecyclerView.Adapter<ColorListAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]
        holder.bind(color)
    }

    override fun getItemCount(): Int = colors.size

    inner class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val colorSquare: View = itemView.findViewById(R.id.colorSquare)
        private val colorName: TextView = itemView.findViewById(R.id.colorName)

        fun bind(color: Color) {
            colorSquare.setBackgroundColor(AndroidColor.rgb(color.r, color.g, color.b))
            colorName.text = color.name
            itemView.setOnClickListener {
                onColorClick(color)
            }
        }
    }

    fun updateData(newColors: List<Color>) {
        colors = newColors
        notifyDataSetChanged()
    }
}
