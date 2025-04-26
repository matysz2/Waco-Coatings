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
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.waco.R
import com.example.waco.adapter.ColorRGBAdapter
import com.example.waco.data.ColorRgbItem

class ColorRGBListActivity : AppCompatActivity() {

    private lateinit var adapter: ColorRGBAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private val colorList = mutableListOf<ColorRgbItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "KOLORY NCS 2050"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        adapter = ColorRGBAdapter(colorList) { selectedColor ->
            showColorDetails(selectedColor)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchColorsFromServer()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = colorList.filter {
                    it.name.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateData(filteredList)
                return true
            }
        })
    }

    private fun fetchColorsFromServer() {
        val url = "http://waco.atwebpages.com/waco/get_colors_rgb.php"

        val request = JsonArrayRequest(
            url,
            { response ->
                val newList = mutableListOf<ColorRgbItem>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val name = obj.getString("name")
                    val r = obj.getInt("r")
                    val g = obj.getInt("g")
                    val b = obj.getInt("b")

                    val item = ColorRgbItem(name, r, g, b)
                    newList.add(item)
                }
                colorList.clear()
                colorList.addAll(newList)
                adapter.updateData(colorList)
                Log.d("ColorRGBListActivity", "Załadowano ${newList.size} kolorów")
            },
            { error ->
                Log.e("VolleyError", "Błąd połączenia: ${error.networkResponse?.statusCode} ${error.message}")
                Toast.makeText(this, "Błąd połączenia", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun showColorDetails(color: ColorRgbItem) {
        val intent = Intent(this, ColorRGBDetailActivity::class.java).apply {
            putExtra("r", color.r)
            putExtra("g", color.g)
            putExtra("b", color.b)
            putExtra("name", color.name)
        }
        startActivity(intent)
    }
}
