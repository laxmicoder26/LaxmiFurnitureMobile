package com.furnitureshop.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.adapter.ProductAdapter
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.data.entity.WishlistEntity
import com.furnitureshop.mobile.ui.admin.AdminProductListActivity
import com.furnitureshop.mobile.ui.admin.HostDashboardActivity
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.SessionManager
import com.furnitureshop.mobile.util.WishlistManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var categoryContainer: LinearLayout
    private lateinit var recycler: RecyclerView
    private lateinit var tvResultsCount: TextView
    private lateinit var tvSort: TextView
    private lateinit var tvCartBadge: TextView
    private lateinit var etSearch: EditText
    private lateinit var adapter: ProductAdapter

    private val allProducts = mutableListOf<ProductEntity>()
    private var activeCategory = "All"
    private var searchTerm = ""
    private val sortOptions = listOf("Featured", "Price: Low to High", "Price: High to Low", "Name: A-Z")
    private var sortIndex = 0
    private val categories = listOf("All", "Sofa", "Bed", "Chair", "Wardrobe", "Table", "Others")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        categoryContainer = findViewById(R.id.categoryContainer)
        recycler = findViewById(R.id.recyclerProducts)
        tvResultsCount = findViewById(R.id.tvResultsCount)
        tvSort = findViewById(R.id.tvSort)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        etSearch = findViewById(R.id.etSearch)

        findViewById<View>(R.id.btnMenu).setOnClickListener { drawerLayout.openDrawer(Gravity.START) }
        findViewById<View>(R.id.btnCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        buildCategoryChips()
        setupRecycler()
        setupSearch()
        setupSort()
        setupDrawer()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        refreshDrawerHeader()
        updateCartBadge()
        loadData()   // picks up admin changes and wishlist changes made on other screens
    }

    private fun setupRecycler() {
        adapter = ProductAdapter(
            emptyList(),
            onClick = { p ->
                startActivity(Intent(this, ProductDetailActivity::class.java).putExtra("productId", p.id))
            },
            onAddToCart = { p -> CartManager.add(p); updateCartBadge() },
            onToggleWishlist = { p -> toggleWishlist(p) }
        )
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = adapter
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
        adapter.notifyDataSetChanged()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val db = AppDatabase.get(applicationContext)
            val email = SessionManager.email(applicationContext)
            if (email != null) {
                WishlistManager.clear()
                WishlistManager.ids.addAll(db.wishlistDao().getWishlistIds(email))
            }
            allProducts.clear()
            allProducts.addAll(db.productDao().getAll())
            applyFilters()
        }
    }

    private fun applyFilters() {
        var list = allProducts.filter {
            (activeCategory == "All" || it.category == activeCategory) &&
                (searchTerm.isEmpty() || it.name.lowercase().contains(searchTerm))
        }
        list = when (sortOptions[sortIndex]) {
            "Price: Low to High" -> list.sortedBy { it.price }
            "Price: High to Low" -> list.sortedByDescending { it.price }
            "Name: A-Z" -> list.sortedBy { it.name }
            else -> list
        }
        tvResultsCount.text = "${list.size} product(s) found"
        adapter.submitList(list)
    }

    private fun buildCategoryChips() {
        categoryContainer.removeAllViews()
        categories.forEach { cat ->
            val chip = TextView(this)
            chip.text = cat
            chip.textSize = 13f
            chip.setPadding(36, 16, 36, 16)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.marginEnd = 16
            chip.layoutParams = lp
            styleChip(chip, cat == activeCategory)
            chip.setOnClickListener {
                activeCategory = cat
                for (i in 0 until categoryContainer.childCount) {
                    val c = categoryContainer.getChildAt(i) as TextView
                    styleChip(c, c.text.toString() == activeCategory)
                }
                applyFilters()
            }
            categoryContainer.addView(chip)
        }
    }

    private fun styleChip(chip: TextView, selected: Boolean) {
        chip.setBackgroundResource(if (selected) R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected)
        chip.setTextColor(getColor(if (selected) R.color.white else R.color.text_dark))
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchTerm = s?.toString()?.trim()?.lowercase() ?: ""
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSort() {
        tvSort.setOnClickListener {
            sortIndex = (sortIndex + 1) % sortOptions.size
            tvSort.text = "Sort: ${sortOptions[sortIndex]} \u25BE"
            applyFilters()
        }
    }

    private fun setupDrawer() {
        navView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.nav_wishlist -> startActivity(Intent(this, WishlistActivity::class.java))
                R.id.nav_orders -> requireLoginThen { startActivity(Intent(this, OrderHistoryActivity::class.java)) }
                R.id.nav_profile -> requireLoginThen { startActivity(Intent(this, EditProfileActivity::class.java)) }
                R.id.nav_admin -> startActivity(Intent(this, AdminProductListActivity::class.java))
                R.id.nav_host -> startActivity(Intent(this, HostDashboardActivity::class.java))
                R.id.nav_become_host -> confirmBecomeHost()
                R.id.nav_logout -> doLogout()
            }
            true
        }
    }

    private fun requireLoginThen(action: () -> Unit) {
        if (SessionManager.isLoggedIn(this)) action()
        else startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun confirmBecomeHost() {
        AlertDialog.Builder(this)
            .setTitle("Become a Host")
            .setMessage("Host ban kar aap apna furniture list karke bech sakte hain, aur sales/earnings dekh sakte hain.")
            .setPositiveButton("Become a Host") { _, _ ->
                val email = SessionManager.email(this) ?: return@setPositiveButton
                lifecycleScope.launch {
                    val dao = AppDatabase.get(applicationContext).userDao()
                    val user = dao.findByEmail(email) ?: return@launch
                    dao.update(user.copy(isHost = true))
                    SessionManager.setHost(applicationContext, true)
                    refreshDrawerHeader()
                    Toast.makeText(this@MainActivity, "You are now a Host!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun doLogout() {
        SessionManager.logout(this)
        WishlistManager.clear()
        CartManager.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun refreshDrawerHeader() {
        val header = navView.getHeaderView(0)
        val tvName = header.findViewById<TextView>(R.id.tvDrawerName)
        val tvEmail = header.findViewById<TextView>(R.id.tvDrawerEmail)
        val tvAvatar = header.findViewById<TextView>(R.id.tvDrawerAvatar)

        if (SessionManager.isLoggedIn(this)) {
            val name = SessionManager.name(this) ?: "User"
            tvName.text = name + when {
                SessionManager.isAdmin(this) -> " (Admin)"
                SessionManager.isHost(this) -> " (Host)"
                else -> ""
            }
            tvEmail.text = SessionManager.email(this)
            tvAvatar.text = name.take(1).uppercase()
        } else {
            tvName.text = "Guest User"
            tvEmail.text = "Login to sync your account"
            tvAvatar.text = "G"
        }
        navView.menu.findItem(R.id.nav_admin).isVisible = SessionManager.isAdmin(this)
        navView.menu.findItem(R.id.nav_host).isVisible = SessionManager.isHost(this)
        navView.menu.findItem(R.id.nav_become_host).isVisible = SessionManager.isLoggedIn(this) &&
            !SessionManager.isHost(this) && !SessionManager.isAdmin(this)
    }

    private fun updateCartBadge() {
        tvCartBadge.text = CartManager.totalItems().toString()
    }
}
