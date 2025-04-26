package com.example.waco.components

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waco.R

class ColorDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Strzałka "wstecz"

        val name = intent.getStringExtra("name") ?: "Brak nazwy"
        val hex = intent.getStringExtra("hex") ?: "#FFFFFF"


       fun onSupportNavigateUp(): Boolean {
            // Przechodzi do ColorListActivity
            val intent = Intent(this, ColorListActivity::class.java)
            startActivity(intent)
            finish() // Kończy ColorDetailActivity
            return true
        }
        // Ustawienie tytułu Toolbar na nazwę koloru
        supportActionBar?.title = name
        // Tworzenie layoutu
        val root = LinearLayout(this).apply {
            setBackgroundColor(Color.parseColor(hex)) // Ustawiamy tło na podstawie HEX
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)

            addView(TextView(this@ColorDetailActivity).apply {
                text = "$name"
                textSize = 28f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
            })
        }

        setContentView(root)
    }
}
