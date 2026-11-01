package com.furnitureshop.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: String,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val hostEmail: String = ""
)
