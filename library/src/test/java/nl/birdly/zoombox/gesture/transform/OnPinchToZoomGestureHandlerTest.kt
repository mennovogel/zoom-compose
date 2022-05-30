package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import junit.framework.TestCase.assertEquals
import nl.birdly.zoombox.Zoom
import org.junit.Test

class OnPinchToZoomGestureHandlerTest {

    private val handler = OnPinchToZoomGestureHandler()

    @Test
    fun `Touch only does not zoom`() {
        var updatedZoom: Zoom? = null

        invoke(
            centroid = Offset(50f, 50f)
        ) {
            updatedZoom = it
        }

        assertEquals(Zoom(), updatedZoom)
    }

    @Test
    fun `Zoom to third handles correctly`() {
        var updatedZoom: Zoom? = null

        invoke(
            centroid = Offset(50f, 50f),
            gestureZoom = 2f
        ) {
            updatedZoom = it
        }

        // TODO: Fix, this is probably not correct.
        assertEquals(Zoom(scale = 2f, offset = Offset(25.0f, 25.0f)), updatedZoom)
    }

    fun invoke(
        centroid: Offset,
        zoomRange: ClosedFloatingPointRange<Float> = 1.0f..2.0f,
        pan: Offset = Offset(0f, 0f),
        zoom: Zoom = Zoom(),
        gestureZoom: Float = 0f,
        gestureRotate: Float = 0f,
        rotation: Boolean = false,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        handler.invoke(
            centroid,
            zoomRange,
            pan,
            zoom,
            gestureZoom,
            gestureRotate,
            rotation,
            onZoomUpdated
        )
    }
}