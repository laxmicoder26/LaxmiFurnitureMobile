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

class AdminProductAdapter(
    private var items: List<ProductEntity>,
    private val onEdit: (ProductEntity) -> Unit,
    private val onDelete: (ProductEntity) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.ivProduct)
        val name: TextView = view.findViewById(R.id.tvName)
        val info: TextView = view.findViewById(R.id.tvInfo)
        val edit: TextView = view.findViewById(R.id.btnEdit)
        val delete: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_product, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.img.setImageResource(ImageUtil.forProduct(p.name))
        holder.name.text = p.name
        holder.info.text = "${p.category}  \u2022  ${Money.format(p.price)}  \u2022  " +
            if (p.inStock) "In stock" else "Out of stock"
        holder.edit.setOnClickListener { onEdit(p) }
        holder.delete.setOnClickListener { onDelete(p) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<ProductEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
