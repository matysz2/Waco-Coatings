package com.example.waco.components

import android.content.Intent
import android.os.Build
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.waco.MainActivity
import com.example.waco.R
import jp.wasabeef.glide.transformations.BlurTransformation

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

        // Znajdź tło
        val backgroundImage = findViewById<ImageView>(R.id.background_image)

        // Profesjonalne rozmycie tła
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ natywne rozmycie
            val blurEffect = RenderEffect.createBlurEffect(
                20f, // promień rozmycia X
                20f, // promień rozmycia Y
                Shader.TileMode.CLAMP
            )
            backgroundImage.setRenderEffect(blurEffect)
        } else {
            // Starsze Androidy - rozmycie Glide
            Glide.with(this)
                .load(R.drawable.tlo)
                .transform(BlurTransformation(25, 3))
                .into(backgroundImage)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }
}
