package com.furnitureshop.mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.util.ImageUtil
import com.furnitureshop.mobile.util.Money

class WishlistAdapter(
    private var items: List<ProductEntity>,
    private val onAddToCart: (ProductEntity) -> Unit,
    private val onRemove: (ProductEntity) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.ivProduct)
        val name: TextView = view.findViewById(R.id.tvName)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val addBtn: TextView = view.findViewById(R.id.btnAddToCart)
        val remove: TextView = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_wishlist, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.img.setImageResource(ImageUtil.forProduct(p.name))
        holder.name.text = p.name
        holder.price.text = Money.format(p.price)
        holder.addBtn.text = if (p.inStock) "Add to Cart" else "Out of Stock"
        holder.addBtn.alpha = if (p.inStock) 1f else 0.5f
        holder.addBtn.setOnClickListener { if (p.inStock) onAddToCart(p) }
        holder.remove.setOnClickListener { onRemove(p) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<ProductEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
