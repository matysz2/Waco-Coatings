package com.example.waco.components

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.waco.R

class ColorRGBDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val r = intent.getIntExtra("r", 255)
        val g = intent.getIntExtra("g", 255)
        val b = intent.getIntExtra("b", 255)
        val name = intent.getStringExtra("name") ?: "Brak nazwy"

        val view = TextView(this).apply {
            setBackgroundColor(Color.rgb(r, g, b))
            text = name
            textSize = 24f
            setTextColor(Color.BLACK)
            setPadding(32, 32, 32, 32)
        }

        setContentView(view)
    }
}
