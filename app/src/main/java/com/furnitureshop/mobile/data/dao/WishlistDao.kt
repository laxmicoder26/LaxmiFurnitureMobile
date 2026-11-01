package com.furnitureshop.mobile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furnitureshop.mobile.data.entity.WishlistEntity

@Dao
interface WishlistDao {
    @Query("SELECT productId FROM wishlist WHERE userEmail = :email")
    suspend fun getWishlistIds(email: String): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE userEmail = :email AND productId = :productId")
    suspend fun remove(email: String, productId: Int)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeAllForProduct(productId: Int)
}
