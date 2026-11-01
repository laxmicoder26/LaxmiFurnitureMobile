package com.furnitureshop.mobile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furnitureshop.mobile.data.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE hostEmail = :email ORDER BY id DESC")
    suspend fun getByHost(email: String): List<ProductEntity>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Query("SELECT MAX(id) FROM products")
    suspend fun maxId(): Int?

    @Insert
    suspend fun insert(product: ProductEntity)

    @Insert
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Int)
}
