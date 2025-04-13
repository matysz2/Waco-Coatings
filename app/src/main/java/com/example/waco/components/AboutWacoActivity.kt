package com.example.waco.components

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waco.MainActivity
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

    override fun onSupportNavigateUp(): Boolean {
        // Zamiast używać onBackPressed, przekierowujemy do konkretnej aktywności
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)  // Uruchamiamy MainActivity
        finish()  // Zakończ obecna aktywność, aby nie pozostała w stosie
        return true
    }
    override fun onBackPressed() {
        // Tworzenie okna dialogowego potwierdzenia
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { dialog, id ->
                // Wywołanie standardowej akcji wyjścia (kończy aktywność)
                super.onBackPressed()
            }
            .setNegativeButton("Nie") { dialog, id ->
                dialog.dismiss()  // Anulowanie zamknięcia aplikacji
            }

        // Wyświetlenie okna dialogowego
        val alert = builder.create()
        alert.show()
    }
}
