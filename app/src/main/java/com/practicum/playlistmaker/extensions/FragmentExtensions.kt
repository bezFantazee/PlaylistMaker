package com.practicum.playlistmaker.extensions

import androidx.fragment.app.Fragment
import androidx.window.layout.WindowMetricsCalculator

fun Fragment.calculateAdaptiveSpanCount(
    cardWidthPx: Int,
    spacingPx: Int,
    minSpanCount: Int = 2
): Int {
    val windowMetric = WindowMetricsCalculator
        .getOrCreate()
        .computeCurrentWindowMetrics(requireActivity())

    val screenWidth = windowMetric.bounds.width()

    return (screenWidth / (cardWidthPx + spacingPx))
        .coerceAtLeast(minSpanCount)
}