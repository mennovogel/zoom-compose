package nl.birdly.zoombox

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

data class ZoomState(
    val scale: Float = 1f,
    val angle: Float = 0f,
    val offset: Offset = Offset.Zero,
    val childRect: Rect? = null
)