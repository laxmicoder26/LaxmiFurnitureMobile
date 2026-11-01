package com.furnitureshop.mobile.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.furnitureshop.mobile.data.entity.OrderEntity
import com.furnitureshop.mobile.data.entity.OrderItemEntity

/** One sold line of a host's product, joined with its order date. */
data class HostSale(
    val orderId: String,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val placedAt: Long
)

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity)

    @Insert
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM orders WHERE userEmail = :email ORDER BY placedAt DESC")
    suspend fun getOrdersForUser(email: String): List<OrderEntity>

    @Query(
        "SELECT oi.orderId AS orderId, oi.productName AS productName, oi.unitPrice AS unitPrice, " +
            "oi.quantity AS quantity, o.placedAt AS placedAt FROM order_items oi " +
            "INNER JOIN orders o ON o.orderId = oi.orderId " +
            "WHERE oi.hostEmail = :email ORDER BY o.placedAt DESC"
    )
    suspend fun getSalesForHost(email: String): List<HostSale>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: String): List<OrderItemEntity>
}
