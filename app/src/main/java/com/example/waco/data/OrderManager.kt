package com.example.waco.data

object OrderManager {
    private val currentOrder: MutableList<Product> = mutableListOf()
    private var comment: String? = null

    fun getCurrentOrder(): List<Product> = currentOrder

    fun addProduct(product: Product) {
        currentOrder.add(product)
    }

    fun removeProductAt(position: Int) {
        if (position in currentOrder.indices) {
            currentOrder.removeAt(position)
        }
    }

    fun clear() {
        currentOrder.clear()
        comment = null
    }

    fun setComment(text: String?) {
        comment = text?.takeIf { it.isNotBlank() }
    }

    fun getComment(): String? = comment

    fun hasComment(): Boolean = !comment.isNullOrEmpty()
}
