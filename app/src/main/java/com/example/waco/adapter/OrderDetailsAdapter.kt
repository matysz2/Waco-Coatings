import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.OrderItem

class OrderDetailsAdapter(private var items: List<OrderItem>) :
    RecyclerView.Adapter<OrderDetailsAdapter.ItemViewHolder>() {

    // ViewHolder, trzyma referencje do elementów widoku
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productNameText)
        val quantity: TextView = view.findViewById(R.id.productQuantityText)
    }

    // Tworzenie ViewHoldera
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_details, parent, false)
        return ItemViewHolder(view)
    }

    // Przypisanie danych do widoków w ViewHolderze
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.productName.text = item.productName
        holder.quantity.text = "Ilość: ${item.quantity}"
    }

    // Liczba elementów w RecyclerView
    override fun getItemCount(): Int = items.size

    // Metoda do aktualizacji danych w adapterze
    fun updateItems(newItems: List<OrderItem>) {
        items = newItems
        notifyDataSetChanged()  // Powiadamia RecyclerView, że dane zostały zmienione
    }
}
