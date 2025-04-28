package com.example.waco.components

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.waco.R

class ColorResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_result)

        val colorName = intent.getStringExtra("colorName") ?: "Brak danych"
        val colorHex = intent.getStringExtra("colorHex") ?: "#000000"
        val colorRGB = intent.getStringExtra("colorRGB") ?: "0,0,0"

        val textColorName = findViewById<TextView>(R.id.textColorName)
        val textColorHex = findViewById<TextView>(R.id.textColorHex)
        val textColorRGB = findViewById<TextView>(R.id.textColorRGB)
        val btnReturn = findViewById<Button>(R.id.btnReturn)
        val btnShare = findViewById<Button>(R.id.btnShare)

        textColorName.text = "Nazwa: $colorName"
        textColorHex.text = "HEX: $colorHex"
        textColorRGB.text = "RGB: $colorRGB"

        val rootView = findViewById<LinearLayout>(R.id.rootView) // Poprawione
        rootView.setBackgroundColor(Color.parseColor(colorHex))

        btnReturn.setOnClickListener {
            finish()
        }

        btnShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Kolor: $colorName\nHEX: $colorHex\nRGB: $colorRGB")
            }
            startActivity(Intent.createChooser(shareIntent, "Udostępnij kolor przez:"))
        }
    }
}
