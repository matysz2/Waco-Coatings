package com.example.waco.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.waco.R
import com.example.waco.data.Invoice

class InvoiceAdapter(
    private val invoices: List<Invoice>,
    private val onDownloadClick: (Invoice) -> Unit
) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val invoiceNumberTextView: TextView = itemView.findViewById(R.id.invoiceNumberTextView)
        val invoiceDateTextView: TextView = itemView.findViewById(R.id.invoiceDateTextView)
        val invoiceAmountTextView: TextView = itemView.findViewById(R.id.invoiceAmountTextView)
        val invoiceStatusTextView: TextView = itemView.findViewById(R.id.invoiceStatusTextView)
        val downloadButton: Button = itemView.findViewById(R.id.downloadInvoiceButton)
        val invoiceGrossValueTextView: TextView = itemView.findViewById(R.id.invoiceGrossValueTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.invoiceNumberTextView.text = "Numer faktury: ${invoice.invoice_number}"
        holder.invoiceDateTextView.text = "Data: ${invoice.date}"
        holder.invoiceAmountTextView.text = "Kwota netto: ${invoice.amount} zł"
        holder.invoiceGrossValueTextView.text = "Kwota brutto: ${invoice.gross_value} zł"
        holder.invoiceStatusTextView.text = "Status: ${invoice.status}"
        holder.downloadButton.setOnClickListener {
            onDownloadClick(invoice)
        }


    }

    override fun getItemCount(): Int = invoices.size
}
