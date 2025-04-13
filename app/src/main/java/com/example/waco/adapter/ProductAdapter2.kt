package com.example.waco.adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Product

class ProductAdapter2(
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter2.ProductViewHolder>() {

    private val products = mutableListOf<Product>()
    private val filteredProducts = mutableListOf<Product>()

    fun setData(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        filteredProducts.clear()
        filteredProducts.addAll(newList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val lowerQuery = query.lowercase()
        filteredProducts.clear()
        filteredProducts.addAll(
            products.filter { it.name.lowercase().contains(lowerQuery) }
        )
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product2, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = filteredProducts.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(filteredProducts[position])
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameText: TextView = itemView.findViewById(R.id.productNameText)
        private val addButton: Button = itemView.findViewById(R.id.addButton)

        fun bind(product: Product) {
            productNameText.text = product.name
            addButton.setOnClickListener {
                onAddClick(product)
            }
        }
    }
}
