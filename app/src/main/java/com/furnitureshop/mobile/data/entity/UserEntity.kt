package com.furnitureshop.mobile.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val password: String,
    val isAdmin: Boolean,
    val isHost: Boolean = false
)
