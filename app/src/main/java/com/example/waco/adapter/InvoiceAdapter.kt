package com.example.waco.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Invoice

class InvoiceAdapter(private val invoices: List<Invoice>) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invoice, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.textNumer.text = "Numer: ${invoice.invoice_number}"
        holder.textKwota.text = "Kwota: ${invoice.amount} zł"
        holder.textTermin.text = "Data: ${invoice.date} (${invoice.status})"
    }

    override fun getItemCount(): Int = invoices.size

    class InvoiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNumer: TextView = view.findViewById(R.id.textNumer)
        val textKwota: TextView = view.findViewById(R.id.textKwota)
        val textTermin: TextView = view.findViewById(R.id.textTermin)
    }
}
