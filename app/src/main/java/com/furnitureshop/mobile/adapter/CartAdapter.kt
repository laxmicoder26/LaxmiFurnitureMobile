package com.furnitureshop.mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.util.CartItem
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.ImageUtil
import com.furnitureshop.mobile.util.Money

class CartAdapter(
    private var items: List<CartItem>,
    private val onChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.ivProduct)
        val name: TextView = view.findViewById(R.id.tvName)
        val subtotal: TextView = view.findViewById(R.id.tvSubtotal)
        val qty: TextView = view.findViewById(R.id.tvQty)
        val minus: TextView = view.findViewById(R.id.btnMinus)
        val plus: TextView = view.findViewById(R.id.btnPlus)
        val remove: TextView = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val p = item.product
        holder.img.setImageResource(ImageUtil.forProduct(p.name))
        holder.name.text = p.name
        holder.subtotal.text = Money.format(item.subtotal)
        holder.qty.text = item.quantity.toString()

        holder.minus.setOnClickListener { CartManager.decrease(p.id); onChanged() }
        holder.plus.setOnClickListener { CartManager.increase(p.id); onChanged() }
        holder.remove.setOnClickListener { CartManager.remove(p.id); onChanged() }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
