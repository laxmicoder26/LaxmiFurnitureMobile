package com.furnitureshop.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val inStock: Boolean,
    val hostEmail: String = ""   // empty = sold by the shop itself
)
