package com.minar.tasticalendar.utilities

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors


// Get the color for an attribute with MaterialColors, or with resolveAttribute. If it doesn't exist, fallback
fun getThemeColor(@AttrRes attrRes: Int, context: Context, @ColorInt fallback: Int = Color.BLACK): Int {
    return try {
        // Get the color using MaterialColors
        MaterialColors.getColor(context, attrRes, fallback)
    } catch (_: Exception) {
        val tv = TypedValue()
        val resolved = context.theme.resolveAttribute(attrRes, tv, true)
        if (!resolved) return fallback
        return if (tv.resourceId != 0) {
            ContextCompat.getColor(context, tv.resourceId)
        } else {
            tv.data
        }
    }
}


// Return a color to maximize the visibility on another color
fun getBestContrast(color: Int, context: Context, alpha: Int = 255, isDark: Boolean? = null): Int {
    // Calculate the perceptive luminance
    val luma =
        (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    // Return black for bright colors, white for dark colors
    return if (alpha < 80) {
        getThemeColor(com.google.android.material.R.attr.colorOnSurface, context)
    } else
        if (luma > 0.5) {
            // Brighter color, darker text
            if (isDark != null && isDark) getThemeColor(com.google.android.material.R.attr.colorOnSurfaceInverse, context)
            if (isDark != null && !isDark) getThemeColor(com.google.android.material.R.attr.colorOnSurface, context)
            Color.BLACK
        } else {
            // Darker color, brighter text
            if (isDark != null && isDark) getThemeColor(com.google.android.material.R.attr.colorOnSurface, context)
            if (isDark != null && !isDark) getThemeColor(com.google.android.material.R.attr.colorOnSurfaceInverse, context)
            Color.WHITE
        }
}