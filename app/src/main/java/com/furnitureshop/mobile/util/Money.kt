package com.furnitureshop.mobile.util

import java.text.NumberFormat
import java.util.Locale

object Money {
    private val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    fun format(amount: Double): String = format.format(amount)
}
