package nl.birdly.zoom.util

import kotlin.math.max
import kotlin.math.min

fun minMax(min: Float, max: Float, value: Float) = min(max, max(min, value))