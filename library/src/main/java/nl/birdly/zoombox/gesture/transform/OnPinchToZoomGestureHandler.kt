package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.minMax

class OnPinchToZoomGestureHandler : (Offset, ClosedFloatingPointRange<Float>, Offset, ZoomState, Float, Float, Boolean, (ZoomState) -> Unit) -> Unit {

    override fun invoke(
        centroid: Offset, // The position in pixels of the centre zoom position where 0,0 is the
        // top left corner
        zoomRange: ClosedFloatingPointRange<Float>,
        pan: Offset,
        zoomState: ZoomState,
        gestureZoom: Float,
        gestureRotate: Float,
        rotation: Boolean,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        val newZoom = minMax(
            zoomRange.start,
            zoomRange.endInclusive,
            gestureZoom * zoomState.scale
        )
        onZoomUpdated(zoomState.copy(
            scale = newZoom,
            offset = Offset(
                zoomState.offset.x + -pan.x * newZoom + (newZoom - zoomState.scale) * centroid.x,
                zoomState.offset.y + -pan.y * newZoom + (newZoom - zoomState.scale) * centroid.y,
            )
        ))
    }
}
