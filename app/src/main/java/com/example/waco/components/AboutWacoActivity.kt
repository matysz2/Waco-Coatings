package com.example.waco.components

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waco.R

class AboutWacoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_waco)

        // Inicjalizacja Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Ustawienie tytułu i strzałki powrotu
        supportActionBar?.apply {
            title = "O WACO"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    // Obsługa kliknięcia strzałki powrotu
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

