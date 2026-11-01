package com.furnitureshop.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.adapter.WishlistAdapter
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.SessionManager
import com.furnitureshop.mobile.util.WishlistManager
import kotlinx.coroutines.launch

class WishlistActivity : AppCompatActivity() {

    private lateinit var adapter: WishlistAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        recycler = findViewById(R.id.recyclerWishlist)
        tvEmpty = findViewById(R.id.tvEmpty)
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        adapter = WishlistAdapter(
            emptyList(),
            onAddToCart = { p ->
                CartManager.add(p)
                Toast.makeText(this, "${p.name} cart me add ho gaya", Toast.LENGTH_SHORT).show()
            },
            onRemove = { p -> removeFromWishlist(p) }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        load()
    }

    private fun load() {
        lifecycleScope.launch {
            val all = AppDatabase.get(applicationContext).productDao().getAll()
            val list: List<ProductEntity> = all.filter { WishlistManager.contains(it.id) }
            adapter.submitList(list)
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun removeFromWishlist(p: ProductEntity) {
        WishlistManager.remove(p.id)
        val email = SessionManager.email(this)
        lifecycleScope.launch {
            if (email != null) {
                AppDatabase.get(applicationContext).wishlistDao().remove(email, p.id)
            }
            load()
        }
    }
}
