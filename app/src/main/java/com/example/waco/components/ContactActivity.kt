package com.example.waco.components

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.waco.MainActivity
import com.example.waco.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ContactActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kontakt"

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
        return true
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val wacoLocation = LatLng(50.034631011130266, 20.984476143078854) // Tarnów

        // Dodanie markera z opisem (snippet)
        googleMap.addMarker(
            MarkerOptions()
                .position(wacoLocation)
                .title("Waco Coatings")
                .snippet("Lakiery do drewna – ul. Spokojna 6, Tarnów")
        )

        // Ustawienie maksymalnego powiększenia (zoom level: 21)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wacoLocation, 16f))

        // Można też ustawić ograniczenia zoomu, jeśli chcesz:
        googleMap.setMinZoomPreference(10f)
        googleMap.setMaxZoomPreference(21f)
    }
}
