package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.minMax

class OnPinchToZoomGestureHandler : (Offset, ClosedFloatingPointRange<Float>, Offset, ZoomState, Float, Float, (ZoomState) -> Unit) -> Unit {

    override fun invoke(
        // The position in pixels of the centre zoom position where 0,0 is the top left corner
        centroid: Offset,
        zoomRange: ClosedFloatingPointRange<Float>,
        pan: Offset,
        zoomState: ZoomState,
        gestureZoom: Float,
        gestureRotate: Float,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        val newScale = minMax(
            zoomRange.start,
            zoomRange.endInclusive,
            gestureZoom * zoomState.scale
        )
        onZoomUpdated(zoomState.copy(
            scale = newScale,
            offset = Offset(
                zoomState.offset.x + -pan.x * newScale + (newScale - zoomState.scale) * centroid.x,
                zoomState.offset.y + -pan.y * newScale + (newScale - zoomState.scale) * centroid.y,
            )
        ))
    }
}
