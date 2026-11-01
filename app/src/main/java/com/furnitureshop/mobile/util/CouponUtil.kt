package com.furnitureshop.mobile.util

object CouponUtil {
    data class Result(val valid: Boolean, val discount: Double, val message: String)

    private val percentCoupons = mapOf("LAXMI10" to 0.10, "WELCOME15" to 0.15)
    private val flatCoupons = mapOf("FLAT500" to 500.0)

    fun hint(): String = "Try: LAXMI10 (10% off), WELCOME15 (15% off), FLAT500 (\u20B9500 off)"

    fun apply(code: String?, subtotal: Double): Result {
        if (code.isNullOrBlank()) return Result(false, 0.0, "Coupon code daalein.")
        val c = code.trim().uppercase()
        percentCoupons[c]?.let {
            return Result(true, subtotal * it, "Coupon applied: ${(it * 100).toInt()}% off!")
        }
        flatCoupons[c]?.let {
            val discount = minOf(it, subtotal)
            return Result(true, discount, "Coupon applied: \u20B9${discount.toInt()} off!")
        }
        return Result(false, 0.0, "Invalid coupon code.")
    }
}
