package com.furnitureshop.mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.entity.OrderEntity
import com.furnitureshop.mobile.data.entity.OrderItemEntity
import com.furnitureshop.mobile.util.Money
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OrderWithItems(val order: OrderEntity, val items: List<OrderItemEntity>)

class OrderAdapter(
    private var orders: List<OrderWithItems>
) : RecyclerView.Adapter<OrderAdapter.VH>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val header: TextView = view.findViewById(R.id.tvOrderHeader)
        val items: TextView = view.findViewById(R.id.tvOrderItems)
        val discount: TextView = view.findViewById(R.id.tvOrderDiscount)
        val total: TextView = view.findViewById(R.id.tvOrderTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val o = orders[position]
        holder.header.text = "Order #${o.order.orderId}  \u2022  ${dateFormat.format(Date(o.order.placedAt))}"
        holder.items.text = o.items.joinToString("\n") {
            "${it.productName} x${it.quantity}  -  ${Money.format(it.unitPrice * it.quantity)}"
        }
        if (o.order.discount > 0) {
            holder.discount.visibility = View.VISIBLE
            holder.discount.text = "Discount: -${Money.format(o.order.discount)}"
        } else {
            holder.discount.visibility = View.GONE
        }
        holder.total.text = "Total: ${Money.format(o.order.total)}"
    }

    override fun getItemCount() = orders.size

    fun submitList(newOrders: List<OrderWithItems>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
