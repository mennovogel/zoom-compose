package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import nl.birdly.zoombox.Zoom
import nl.birdly.zoombox.rotateBy
import nl.birdly.zoombox.util.minMax

class OnPinchToZoomGestureHandler : (Offset, ClosedFloatingPointRange<Float>, Offset, Zoom, Float, Float, Boolean, (Zoom) -> Unit) -> Unit {

    override fun invoke(
        centroid: Offset, // The position in pixels of the centre zoom position where 0,0 is the
        // top left corner
        zoomRange: ClosedFloatingPointRange<Float>,
        pan: Offset,
        zoom: Zoom,
        gestureZoom: Float,
        gestureRotate: Float,
        rotation: Boolean,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        val newZoom = minMax(
            zoomRange.start,
            zoomRange.endInclusive,
            gestureZoom * zoom.scale
        )
        onZoomUpdated(zoom.copy(
            scale = newZoom,
            offset = Offset(
                zoom.offset.x + -pan.x * newZoom + (newZoom - zoom.scale) * centroid.x,
                zoom.offset.y + -pan.y * newZoom + (newZoom - zoom.scale) * centroid.y,
            )
        ))
    }
}
