package com.minar.tasticalendar.utilities

import android.view.View
import com.google.android.material.snackbar.Snackbar

// Show a snackbar containing a given text, anchored to a given view
fun showSnackbar(
    content: String,
    baseView: View,
    duration: Int = 5000
) {
    val snackbar = Snackbar.make(baseView, content, duration)
    snackbar.isGestureInsetBottomIgnored = true
    // Anchor the snackbar to the desired view
    snackbar.anchorView = baseView
    snackbar.show()
}