package com.furnitureshop.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.adapter.CartAdapter
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.CouponUtil
import com.furnitureshop.mobile.util.Money

class CartActivity : AppCompatActivity() {

    private lateinit var adapter: CartAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var etCoupon: EditText
    private lateinit var tvCouponStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recycler = findViewById(R.id.recyclerCart)
        tvEmpty = findViewById(R.id.tvEmpty)
        etCoupon = findViewById(R.id.etCoupon)
        tvCouponStatus = findViewById(R.id.tvCouponStatus)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnClear).setOnClickListener {
            CartManager.clear()
            etCoupon.setText("")
            refresh()
        }

        adapter = CartAdapter(CartManager.items.values.toList()) { refresh() }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        tvCouponStatus.text = CouponUtil.hint()
        CartManager.appliedCoupon?.let { etCoupon.setText(it) }

        findViewById<View>(R.id.btnApplyCoupon).setOnClickListener { applyCoupon() }
        findViewById<View>(R.id.btnCheckout).setOnClickListener {
            if (CartManager.items.isEmpty()) {
                Toast.makeText(this, "Your cart is empty. Add some furniture first!", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, CheckoutActivity::class.java))
            }
        }

        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun applyCoupon() {
        val subtotal = CartManager.subtotal()
        if (subtotal <= 0) {
            tvCouponStatus.text = "Pehle cart me product add karein."
            tvCouponStatus.setTextColor(getColor(R.color.red))
            return
        }
        val code = etCoupon.text.toString()
        val result = CouponUtil.apply(code, subtotal)
        if (result.valid) {
            CartManager.appliedCoupon = code.trim().uppercase()
            tvCouponStatus.setTextColor(getColor(R.color.green))
        } else {
            CartManager.appliedCoupon = null
            tvCouponStatus.setTextColor(getColor(R.color.red))
        }
        tvCouponStatus.text = result.message
        refresh()
    }

    private fun refresh() {
        adapter.submitList(CartManager.items.values.toList())
        val empty = CartManager.items.isEmpty()
        tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        recycler.visibility = if (empty) View.GONE else View.VISIBLE

        val discount = CartManager.discount()
        findViewById<TextView>(R.id.tvSubtotal).text = Money.format(CartManager.subtotal())
        findViewById<TextView>(R.id.tvDiscount).text =
            if (discount > 0) "-" + Money.format(discount) else Money.format(0.0)
        findViewById<TextView>(R.id.tvGst).text = Money.format(CartManager.gst())
        findViewById<TextView>(R.id.tvTotal).text = Money.format(CartManager.total())
    }
}
