package com.furnitureshop.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.adapter.OrderAdapter
import com.furnitureshop.mobile.adapter.OrderWithItems
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.util.SessionManager
import kotlinx.coroutines.launch

class OrderHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val recycler = findViewById<RecyclerView>(R.id.recyclerOrders)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        val adapter = OrderAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val email = SessionManager.email(this)
        if (email == null) {
            tvEmpty.text = "Order history dekhne ke liye login karein."
            tvEmpty.visibility = View.VISIBLE
            return
        }

        lifecycleScope.launch {
            val dao = AppDatabase.get(applicationContext).orderDao()
            val result = dao.getOrdersForUser(email).map { OrderWithItems(it, dao.getItemsForOrder(it.orderId)) }
            adapter.submitList(result)
            tvEmpty.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
