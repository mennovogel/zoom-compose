package nl.birdly.zoombox.util

import android.util.Log
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import nl.birdly.zoombox.Zoom

suspend fun TransformableState.animateZoomBy(
    previousZoom: Zoom,
    scale: Float,
    touchPoint: Offset,
    size: IntSize,
    childImageBounds: Rect,
    zoomAnimationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow),
    onZoomUpdated: (Zoom) -> Unit
) {
    Log.d("Menno", "animateZoomBy: $previousZoom, $scale")
    require(scale > 0) {
        "scale value should be greater than 0"
    }
    val translationX = Calculator.calculateFutureTranslation(
        scale,
        touchPoint.x,
        size.width,
        childImageBounds.width.toInt()
    )
    val translationY = Calculator.calculateFutureTranslation(
        scale,
        touchPoint.y,
        size.height,
        childImageBounds.height.toInt()
    )

    animateZoomBy(
        previousZoom,
        previousZoom.copy(
            scale = scale,
            offset = Offset(
                translationX,
                translationY
            )
        ), zoomAnimationSpec, onZoomUpdated
    )
}

suspend fun TransformableState.animateZoomBy(
    previousZoom: Zoom,
    nextZoom: Zoom,
    zoomAnimationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow),
    onZoomUpdated: (Zoom) -> Unit
) {
    var currentZoom = previousZoom
    transform {
        AnimationState(initialValue = 0f).animateTo(1f, zoomAnimationSpec) {
            val newScale = previousZoom.scale + this.value * (nextZoom.scale - previousZoom.scale)

            currentZoom = currentZoom.copy(
                scale = newScale,
                offset = Offset(
                    x = previousZoom.offset.x * (1 - this.value) - nextZoom.offset.x * this.value,
                    y = previousZoom.offset.y * (1 - this.value) - nextZoom.offset.y * this.value
                )
            )
            onZoomUpdated(currentZoom)
        }
    }
}