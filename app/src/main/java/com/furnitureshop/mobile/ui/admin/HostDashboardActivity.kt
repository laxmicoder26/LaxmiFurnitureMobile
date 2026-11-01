package com.furnitureshop.mobile.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.adapter.AdminProductAdapter
import com.furnitureshop.mobile.adapter.SaleAdapter
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.Money
import com.furnitureshop.mobile.util.SessionManager
import com.furnitureshop.mobile.util.WishlistManager
import kotlinx.coroutines.launch

/** Host (seller) dashboard: own listings + sales + earnings. */
class HostDashboardActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tabListings: TextView
    private lateinit var tabSales: TextView
    private lateinit var listingAdapter: AdminProductAdapter
    private lateinit var saleAdapter: SaleAdapter
    private var showSales = false
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val me = SessionManager.email(this)
        if (me == null || !SessionManager.isHost(this)) {
            finish()
            return
        }
        email = me
        setContentView(R.layout.activity_host_dashboard)

        recycler = findViewById(R.id.recyclerHost)
        tvEmpty = findViewById(R.id.tvEmpty)
        tabListings = findViewById(R.id.tabListings)
        tabSales = findViewById(R.id.tabSales)
        findViewById<TextView>(R.id.tvWelcome).text = "Welcome, ${SessionManager.name(this) ?: "Host"}"
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, AdminProductEditActivity::class.java))
        }

        listingAdapter = AdminProductAdapter(emptyList(), onEdit = { p ->
            startActivity(Intent(this, AdminProductEditActivity::class.java).putExtra("productId", p.id))
        }, onDelete = { p -> confirmDelete(p) })
        saleAdapter = SaleAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)

        tabListings.setOnClickListener { showSales = false; render() }
        tabSales.setOnClickListener { showSales = true; render() }
        render()
    }

    override fun onResume() {
        super.onResume()
        if (::recycler.isInitialized) load()
    }

    private fun render() {
        styleTab(tabListings, !showSales)
        styleTab(tabSales, showSales)
        recycler.adapter = if (showSales) saleAdapter else listingAdapter
        load()
    }

    private fun styleTab(tab: TextView, selected: Boolean) {
        tab.setBackgroundResource(if (selected) R.drawable.bg_chip_selected else android.R.color.transparent)
        tab.setTextColor(getColor(if (selected) R.color.white else R.color.text_dark))
    }

    private fun load() {
        lifecycleScope.launch {
            val db = AppDatabase.get(applicationContext)
            val listings = db.productDao().getByHost(email)
            val sales = db.orderDao().getSalesForHost(email)

            findViewById<TextView>(R.id.tvStatListings).text = listings.size.toString()
            findViewById<TextView>(R.id.tvStatUnits).text = sales.sumOf { it.quantity }.toString()
            findViewById<TextView>(R.id.tvStatEarnings).text =
                Money.format(sales.sumOf { it.unitPrice * it.quantity })

            listingAdapter.submitList(listings)
            saleAdapter.submitList(sales)

            val empty = if (showSales) sales.isEmpty() else listings.isEmpty()
            tvEmpty.text = if (showSales) "Abhi tak koi sale nahi hui."
            else "Aapne abhi koi product list nahi kiya.\n\"+ List Product\" se shuru karein."
            tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        }
    }

    private fun confirmDelete(p: ProductEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete listing?")
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
