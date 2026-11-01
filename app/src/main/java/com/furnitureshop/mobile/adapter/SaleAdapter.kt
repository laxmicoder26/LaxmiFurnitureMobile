package com.furnitureshop.mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.dao.HostSale
import com.furnitureshop.mobile.util.Money
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaleAdapter(private var items: List<HostSale>) : RecyclerView.Adapter<SaleAdapter.VH>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvSaleName)
        val info: TextView = view.findViewById(R.id.tvSaleInfo)
        val amount: TextView = view.findViewById(R.id.tvSaleAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        holder.name.text = "${s.productName} x${s.quantity}"
        holder.info.text = "Order #${s.orderId}  \u2022  ${dateFormat.format(Date(s.placedAt))}"
        holder.amount.text = Money.format(s.unitPrice * s.quantity)
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<HostSale>) {
        items = newItems
        notifyDataSetChanged()
    }
}
