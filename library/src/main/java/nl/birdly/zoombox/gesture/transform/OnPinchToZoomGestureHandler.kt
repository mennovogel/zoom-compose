package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import nl.birdly.zoombox.Zoom
import nl.birdly.zoombox.rotateBy
import nl.birdly.zoombox.util.minMax

class OnPinchToZoomGestureHandler : (Offset, ClosedFloatingPointRange<Float>, TransformableState, Offset, Zoom, Float, Float, Boolean, (Zoom) -> Unit) -> Unit {

    override fun invoke(
        centroid: Offset,
        zoomRange: ClosedFloatingPointRange<Float>,
        state: TransformableState,
        pan: Offset,
        zoom: Zoom,
        gestureZoom: Float,
        gestureRotate: Float,
        rotation: Boolean,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        val oldScale = zoom.scale
        val newScale = minMax(
            zoomRange.start,
            zoomRange.endInclusive,
            zoom.scale * gestureZoom
        )
        val newOffset =
            (zoom.offset + centroid / oldScale).rotateBy(gestureRotate) -
                    (centroid / newScale + pan * zoom.scale)
        val newAngle = if (rotation) {
            zoom.angle + gestureRotate
        } else {
            zoom.angle
        }
        onZoomUpdated(Zoom(newScale, newAngle, newOffset))
    }
}
