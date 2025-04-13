package com.example.waco.data

object OrderManager {
    private val currentOrder: MutableList<Product> = mutableListOf()

    fun getCurrentOrder(): List<Product> = currentOrder

    fun addProduct(product: Product) {
        currentOrder.add(product)
    }

    fun removeProductAt(position: Int) {
        currentOrder.removeAt(position)
    }

    fun clear() {
        currentOrder.clear()  // Metoda czyszcząca bieżące zamówienie.
    }



}