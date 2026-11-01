package com.furnitureshop.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.util.Money

class OrderSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        val orderId = intent.getStringExtra("orderId") ?: ""
        val total = intent.getDoubleExtra("total", 0.0)
        val payment = intent.getStringExtra("payment") ?: ""
        val loggedIn = intent.getBooleanExtra("loggedIn", false)

        findViewById<TextView>(R.id.tvOrderId).text = "Order #$orderId"
        findViewById<TextView>(R.id.tvTotal).text = Money.format(total)
        findViewById<TextView>(R.id.tvPayment).text = "Payment: $payment"

        val btnOrders = findViewById<View>(R.id.btnViewOrders)
        if (loggedIn) {
            btnOrders.setOnClickListener {
                startActivity(Intent(this, OrderHistoryActivity::class.java))
                finish()
            }
        } else {
            btnOrders.visibility = View.GONE
            findViewById<TextView>(R.id.tvMessage).text =
                "Our team will contact you shortly for delivery.\nTip: Login karke apni order history save kar sakte hain."
        }

        findViewById<View>(R.id.btnContinue).setOnClickListener { goHome() }
    }

    override fun onBackPressed() {
        goHome()
    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
