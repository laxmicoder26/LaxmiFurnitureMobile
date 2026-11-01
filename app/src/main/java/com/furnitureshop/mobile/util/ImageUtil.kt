package com.furnitureshop.mobile.util

import com.furnitureshop.mobile.R

object ImageUtil {
    fun forProduct(name: String): Int {
        val n = name.lowercase()
        return when {
            n.contains("sofa") || n.contains("couch") -> R.drawable.sofa
            n.contains("bed") -> R.drawable.bed
            n.contains("chair") -> R.drawable.chair
            n.contains("wardrobe") || n.contains("cabinet") -> R.drawable.wardrobe
            else -> R.drawable.placeholder
        }
    }
}
