package com.furnitureshop.mobile.util

import com.furnitureshop.mobile.data.entity.ProductEntity

data class CartItem(val product: ProductEntity, var quantity: Int) {
    val subtotal: Double get() = product.price * quantity
}

/** Session-only cart (cleared after checkout), same design as the desktop app. */
object CartManager {
    val items = LinkedHashMap<Int, CartItem>()
    var appliedCoupon: String? = null

    fun add(product: ProductEntity) {
        val existing = items[product.id]
        if (existing != null) existing.quantity++ else items[product.id] = CartItem(product, 1)
    }

    fun increase(productId: Int) { items[productId]?.let { it.quantity++ } }

    fun decrease(productId: Int) {
        val item = items[productId] ?: return
        if (item.quantity <= 1) items.remove(productId) else item.quantity--
    }

    fun remove(productId: Int) { items.remove(productId) }

    fun clear() {
        items.clear()
        appliedCoupon = null
    }

    fun subtotal(): Double = items.values.sumOf { it.subtotal }
    fun totalItems(): Int = items.values.sumOf { it.quantity }

    fun discount(): Double {
        val code = appliedCoupon ?: return 0.0
        val result = CouponUtil.apply(code, subtotal())
        return if (result.valid) result.discount else 0.0
    }

    fun taxable(): Double = maxOf(0.0, subtotal() - discount())
    fun gst(): Double = taxable() * 0.18
    fun total(): Double = taxable() + gst()
}
