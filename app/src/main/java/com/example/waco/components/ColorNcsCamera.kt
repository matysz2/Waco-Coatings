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
import com.example.waco.adapter.ColorListAdapter
import com.example.waco.camera.CameraViewFragment
import com.example.waco.data.Color

class ColorNcsCamera : AppCompatActivity() {

    private lateinit var adapter: ColorListAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private val colorList = mutableListOf<Color>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kolory NCS 2050"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        adapter = ColorListAdapter(colorList) { selectedColor ->
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
        val url = "http://waco.atwebpages.com/waco/get_colors_rgb.php"
        val request = JsonArrayRequest(
            url,
            { response ->
                val newList = mutableListOf<Color>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val color = Color(
                        obj.getString("name"),
                        obj.getInt("r"),
                        obj.getInt("g"),
                        obj.getInt("b")
                    )
                    newList.add(color)
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

    private fun openCameraWithColor(color: Color) {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, CameraViewFragment.newInstance(color))
            .addToBackStack(null)
            .commit()
    }
}
