package com.furnitureshop.mobile.data.entity

import androidx.room.Entity

@Entity(tableName = "wishlist", primaryKeys = ["userEmail", "productId"])
data class WishlistEntity(
    val userEmail: String,
    val productId: Int
)
