package com.furnitureshop.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val userEmail: String,
    val subtotal: Double,
    val discount: Double,
    val gst: Double,
    val total: Double,
    val placedAt: Long
)
