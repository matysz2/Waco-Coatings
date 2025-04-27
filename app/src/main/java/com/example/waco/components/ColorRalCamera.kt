package com.example.waco.components

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.waco.R
import com.example.waco.adapter.ColorHexListAdapter
import com.example.waco.camera.CameraViewFragment
import com.example.waco.data.ColorHex

class ColorRalCamera : AppCompatActivity() {

    private lateinit var adapter: ColorHexListAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private val colorList = mutableListOf<ColorHex>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kolory HEX"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        adapter = ColorHexListAdapter(colorList) { selectedColor ->
            openCameraWithColor(selectedColor)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchColorsFromServer()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = colorList.filter {
                    it.name.contains(newText.orEmpty(), ignoreCase = true)
                }
                adapter.updateData(filteredList)
                return true
            }
        })
    }

    private fun fetchColorsFromServer() {
        val url = "http://waco.atwebpages.com/waco/get_colors.php"
        val request = JsonArrayRequest(
            url,
            { response ->
                val newList = mutableListOf<ColorHex>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val item = ColorHex(
                        obj.getString("name"),
                        obj.getString("hex")
                    )
                    newList.add(item)
                }
                colorList.clear()
                colorList.addAll(newList)
                adapter.updateData(colorList)
            },
            {
                Toast.makeText(this, "Błąd ładowania kolorów!", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun openCameraWithColor(colorHex: ColorHex) {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, CameraViewFragment.newInstanceHex(colorHex))
            .addToBackStack(null)
            .commit()
    }
}
