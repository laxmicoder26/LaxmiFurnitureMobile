package com.furnitureshop.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.OrderEntity
import com.furnitureshop.mobile.data.entity.OrderItemEntity
import com.furnitureshop.mobile.util.CartManager
import com.furnitureshop.mobile.util.Money
import com.furnitureshop.mobile.util.SessionManager
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var rgPayment: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        rgPayment = findViewById(R.id.rgPayment)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        SessionManager.name(this)?.let { etName.setText(it) }

        val sb = StringBuilder()
        CartManager.items.values.forEach {
            sb.append("${it.product.name} x${it.quantity}  -  ${Money.format(it.subtotal)}\n")
        }
        sb.append("\nSubtotal: ${Money.format(CartManager.subtotal())}")
        val discount = CartManager.discount()
        if (discount > 0) {
            sb.append("\nDiscount (${CartManager.appliedCoupon}): -${Money.format(discount)}")
        }
        sb.append("\nGST (18%): ${Money.format(CartManager.gst())}")
        sb.append("\nGrand Total: ${Money.format(CartManager.total())}")
        findViewById<TextView>(R.id.tvSummary).text = sb.toString()

        val btnPlace = findViewById<TextView>(R.id.btnPlaceOrder)
        btnPlace.text = "Place Order  \u2022  ${Money.format(CartManager.total())}"
        btnPlace.setOnClickListener { placeOrder() }
    }

    private fun placeOrder() {
        if (CartManager.items.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (etName.text.isNullOrBlank() || etPhone.text.isNullOrBlank() || etAddress.text.isNullOrBlank()) {
            Toast.makeText(this, "Naam, phone aur address bharna zaroori hai.", Toast.LENGTH_SHORT).show()
            return
        }

        val payment = when (rgPayment.checkedRadioButtonId) {
            R.id.rbUpi -> "UPI"
            R.id.rbCard -> "Credit / Debit Card"
            else -> "Cash on Delivery"
        }

        val orderId = "LF" + (System.currentTimeMillis() % 100000)
        val subtotal = CartManager.subtotal()
        val discount = CartManager.discount()
        val gst = CartManager.gst()
        val total = CartManager.total()
        val email = SessionManager.email(this)
        val lines = CartManager.items.values.map {
            OrderItemEntity(orderId = orderId, productName = it.product.name,
                unitPrice = it.product.price, quantity = it.quantity,
                hostEmail = it.product.hostEmail)
        }

        lifecycleScope.launch {
            // Always store the order so hosts see every sale. Guest orders are saved under
            // "guest" (they don't appear in any customer's My Orders list).
            val db = AppDatabase.get(applicationContext)
            db.orderDao().insertOrder(
                OrderEntity(orderId, email ?: "guest", subtotal, discount, gst, total, System.currentTimeMillis())
            )
            db.orderDao().insertItems(lines)
            CartManager.clear()

            val intent = Intent(this@CheckoutActivity, OrderSuccessActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("total", total)
            intent.putExtra("payment", payment)
            intent.putExtra("loggedIn", email != null)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
