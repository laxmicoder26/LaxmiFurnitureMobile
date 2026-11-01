package com.furnitureshop.mobile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.furnitureshop.mobile.data.dao.OrderDao
import com.furnitureshop.mobile.data.dao.ProductDao
import com.furnitureshop.mobile.data.dao.UserDao
import com.furnitureshop.mobile.data.dao.WishlistDao
import com.furnitureshop.mobile.data.entity.OrderEntity
import com.furnitureshop.mobile.data.entity.OrderItemEntity
import com.furnitureshop.mobile.data.entity.ProductEntity
import com.furnitureshop.mobile.data.entity.UserEntity
import com.furnitureshop.mobile.data.entity.WishlistEntity

@Database(
    entities = [ProductEntity::class, UserEntity::class, OrderEntity::class,
        OrderItemEntity::class, WishlistEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun wishlistDao(): WishlistDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "laxmi_furniture.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
