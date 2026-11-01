package com.furnitureshop.mobile.ui.admin

import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdminProductEditActivity : AppCompatActivity() {

    private var editingId: Int = -1
    private var existing: ProductEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!SessionManager.isAdmin(this) && !SessionManager.isHost(this)) {
            finish()
            return
        }
        setContentView(R.layout.activity_admin_edit)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etCategory = findViewById<TextInputEditText>(R.id.etCategory)
        val etPrice = findViewById<TextInputEditText>(R.id.etPrice)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val cbInStock = findViewById<CheckBox>(R.id.cbInStock)
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        editingId = intent.getIntExtra("productId", -1)
        if (editingId != -1) {
            findViewById<TextView>(R.id.tvTitle).text = "Edit Product"
            lifecycleScope.launch {
                val p = AppDatabase.get(applicationContext).productDao().getById(editingId)
                // A host may only edit their own listings.
                if (p != null && !SessionManager.isAdmin(this@AdminProductEditActivity) &&
                    p.hostEmail != SessionManager.email(this@AdminProductEditActivity)) {
                    finish()
                    return@launch
                }
                existing = p
                if (p != null) {
                    etName.setText(p.name)
                    etCategory.setText(p.category)
                    etPrice.setText(p.price.toInt().toString())
                    etDescription.setText(p.description)
                    cbInStock.isChecked = p.inStock
                }
            }
        }

        findViewById<TextView>(R.id.btnSave).setOnClickListener {
            val name = etName.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val price = etPrice.text.toString().trim().toDoubleOrNull()
            val description = etDescription.text.toString().trim()

            if (name.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Name aur category bharna zaroori hai.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (price == null || price < 0) {
                Toast.makeText(this, "Price sahi number honi chahiye.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val dao = AppDatabase.get(applicationContext).productDao()
                if (editingId != -1) {
                    dao.update(ProductEntity(editingId, name, category, price, description, cbInStock.isChecked,
                        existing?.hostEmail ?: ""))
                } else {
                    val newId = (dao.maxId() ?: 0) + 1
                    // A host's new product belongs to that host; admin-added products belong to the shop.
                    val owner = if (SessionManager.isAdmin(this@AdminProductEditActivity)) ""
                    else SessionManager.email(this@AdminProductEditActivity) ?: ""
                    dao.insert(ProductEntity(newId, name, category, price, description, cbInStock.isChecked, owner))
                }
                Toast.makeText(this@AdminProductEditActivity, "Product saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
