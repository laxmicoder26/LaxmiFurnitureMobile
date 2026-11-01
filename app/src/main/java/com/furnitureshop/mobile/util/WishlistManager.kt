package com.furnitureshop.mobile.util

object WishlistManager {
    val ids = linkedSetOf<Int>()

    fun clear() = ids.clear()
    fun contains(id: Int) = ids.contains(id)
    fun add(id: Int) = ids.add(id)
    fun remove(id: Int) = ids.remove(id)
}
