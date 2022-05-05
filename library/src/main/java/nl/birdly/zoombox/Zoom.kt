package nl.birdly.zoombox

import androidx.compose.ui.geometry.Offset

data class Zoom(
    val scale: Float = 1f,
    val angle: Float = 0f,
    val offset: Offset = Offset.Zero
)