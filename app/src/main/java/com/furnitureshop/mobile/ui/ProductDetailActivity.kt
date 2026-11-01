package com.furnitureshop.mobile.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.data.entity.WishlistEntity
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.ImageUtil
import com.furnitureshop.mobile.util.Money
import com.furnitureshop.mobile.util.SessionManager
import com.furnitureshop.mobile.util.WishlistManager
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var tvHeart: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val productId = intent.getIntExtra("productId", -1)
        tvHeart = findViewById(R.id.tvHeart)
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        lifecycleScope.launch {
            val p = AppDatabase.get(applicationContext).productDao().getById(productId)
            if (p == null) finish() else bind(p)
        }
    }

    private suspend fun bind(p: ProductEntity) {
        findViewById<ImageView>(R.id.ivProduct).setImageResource(ImageUtil.forProduct(p.name))
        findViewById<TextView>(R.id.tvCategory).text = p.category.uppercase()
        findViewById<TextView>(R.id.tvName).text = p.name
        val seller = if (p.hostEmail.isEmpty()) "Laxmi Furniture"
        else AppDatabase.get(applicationContext).userDao().findByEmail(p.hostEmail)?.name ?: "Host"
        findViewById<TextView>(R.id.tvSeller).text = "Sold by $seller"
        findViewById<TextView>(R.id.tvDescription).text = p.description
        findViewById<TextView>(R.id.tvPrice).text = Money.format(p.price)

        val tvStock = findViewById<TextView>(R.id.tvStock)
        if (p.inStock) {
            tvStock.text = "\u2713 In Stock"
            tvStock.setTextColor(getColor(R.color.green))
        } else {
            tvStock.text = "\u2716 Out of Stock"
            tvStock.setTextColor(getColor(R.color.red))
        }

        updateHeart(p.id)
        tvHeart.setOnClickListener { toggleWishlist(p) }

        val btnAdd = findViewById<TextView>(R.id.btnAddToCart)
        btnAdd.text = if (p.inStock) "Add to Cart" else "Notify Me"
        btnAdd.alpha = if (p.inStock) 1f else 0.5f
        btnAdd.setOnClickListener {
            if (p.inStock) {
                CartManager.add(p)
                Toast.makeText(this, "Cart me add ho gaya", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateHeart(id: Int) {
        tvHeart.text = if (WishlistManager.contains(id)) "\u2665" else "\u2661"
    }

    private fun toggleWishlist(p: ProductEntity) {
        val email = SessionManager.email(this)
        if (WishlistManager.contains(p.id)) {
            WishlistManager.remove(p.id)
            if (email != null) lifecycleScope.launch {
                AppDatabase.get(applicationContext).wishlistDao().remove(email, p.id)
            }
        } else {
            WishlistManager.add(p.id)
            if (email != null) lifecycleScope.launch {
                AppDatabase.get(applicationContext).wishlistDao().add(WishlistEntity(email, p.id))
            }
        }
        updateHeart(p.id)
    }
}
