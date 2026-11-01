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
import com.furnitureshop.mobile.util.WishlistManager

class ProductAdapter(
    private var items: List<ProductEntity>,
    private val onClick: (ProductEntity) -> Unit,
    private val onAddToCart: (ProductEntity) -> Unit,
    private val onToggleWishlist: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.ivProduct)
        val heart: TextView = view.findViewById(R.id.tvHeart)
        val outOfStock: View = view.findViewById(R.id.tvOutOfStock)
        val category: TextView = view.findViewById(R.id.tvCategory)
        val name: TextView = view.findViewById(R.id.tvName)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val addBtn: TextView = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.img.setImageResource(ImageUtil.forProduct(p.name))
        holder.category.text = p.category.uppercase()
        holder.name.text = p.name
        holder.price.text = Money.format(p.price)
        holder.outOfStock.visibility = if (p.inStock) View.GONE else View.VISIBLE

        val liked = WishlistManager.contains(p.id)
        holder.heart.text = if (liked) "\u2665" else "\u2661"
        holder.heart.setTextColor(holder.itemView.context.getColor(if (liked) R.color.red else R.color.muted))

        holder.addBtn.text = if (p.inStock) "Add to Cart" else "Notify Me"
        holder.addBtn.alpha = if (p.inStock) 1f else 0.5f
        holder.addBtn.setOnClickListener { if (p.inStock) onAddToCart(p) }
        holder.heart.setOnClickListener { onToggleWishlist(p) }
        holder.itemView.setOnClickListener { onClick(p) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<ProductEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
