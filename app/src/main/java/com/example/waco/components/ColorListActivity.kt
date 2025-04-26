package com.example.waco.components

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.waco.R
import com.example.waco.adapter.ColorAdapter
import com.example.waco.data.ColorItem

class ColorListActivity : AppCompatActivity() {

    private lateinit var adapter: ColorAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private val colorList = mutableListOf<ColorItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_list)

        // Konfiguracja Toolbara
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "KOLORY RAL CLASSIC K7"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Włącz strzałkę wstecz

        // Obsługa kliknięcia strzałki wstecz
        toolbar.setNavigationOnClickListener {
            finish() // Zakończ aktywność i wróć do poprzedniej
        }

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        adapter = ColorAdapter(colorList) { selectedColor ->
            showColorDetails(selectedColor)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchColorsFromServer()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun fetchColorsFromServer() {
        val url = "http://waco.atwebpages.com/waco/get_colors.php"

        val request = object : JsonArrayRequest(Method.GET, url, null,
            { response ->
                val newList = mutableListOf<ColorItem>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val name = obj.getString("name")
                    val hex = obj.getString("hex")

                    if (hex != "HEX") {
                        val item = ColorItem(name = name, hex = hex)
                        newList.add(item)
                    } else {
                        Log.w("ColorListActivity", "Niepoprawny hex dla koloru: $name")
                    }
                }
                adapter.updateData(newList)
                Log.d("ColorListActivity", "Załadowano ${newList.size} kolorów")
            },
            { error ->
                Log.e("VolleyError", "Błąd połączenia: ${error.networkResponse?.statusCode} ${error.message}")
                Toast.makeText(this, "Błąd połączenia: ${error.networkResponse?.statusCode}", Toast.LENGTH_LONG).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Android WacoApp)" // Podszyj się pod przeglądarkę!
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun showColorDetails(color: ColorItem) {
        val intent = Intent(this, ColorDetailActivity::class.java).apply {
            putExtra("name", color.name)
            putExtra("hex", color.hex)
        }
        startActivity(intent)
    }
}
