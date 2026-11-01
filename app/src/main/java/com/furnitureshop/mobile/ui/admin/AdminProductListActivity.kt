package com.furnitureshop.mobile.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.adapter.AdminProductAdapter
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.SessionManager
import com.furnitureshop.mobile.util.WishlistManager
import kotlinx.coroutines.launch

class AdminProductListActivity : AppCompatActivity() {

    private lateinit var adapter: AdminProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only admins may open this screen.
        if (!SessionManager.isAdmin(this)) {
            finish()
            return
        }
        setContentView(R.layout.activity_admin_list)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, AdminProductEditActivity::class.java))
        }

        adapter = AdminProductAdapter(emptyList(), onEdit = { p ->
            startActivity(Intent(this, AdminProductEditActivity::class.java).putExtra("productId", p.id))
        }, onDelete = { p -> confirmDelete(p) })

        val recycler = findViewById<RecyclerView>(R.id.recyclerAdmin)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (SessionManager.isAdmin(this)) load()
    }

    private fun load() {
        lifecycleScope.launch {
            adapter.submitList(AppDatabase.get(applicationContext).productDao().getAll())
        }
    }

    private fun confirmDelete(p: ProductEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete product?")
            .setMessage("\"${p.name}\" ko delete karna chahte hain?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    val db = AppDatabase.get(applicationContext)
                    db.productDao().deleteById(p.id)
                    db.wishlistDao().removeAllForProduct(p.id)
                    CartManager.remove(p.id)
                    WishlistManager.remove(p.id)
                    load()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
