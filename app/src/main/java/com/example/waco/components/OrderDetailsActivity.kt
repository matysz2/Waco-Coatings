package com.example.waco.components

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.waco.R

class OrderDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        val orderId = intent.getIntExtra("orderId", -1)

        // Pobierz szczegóły zamówienia z backendu
        fetchOrderDetails(orderId)
    }

    private fun fetchOrderDetails(orderId: Int) {
        // Pobierz szczegóły zamówienia
    }
}
