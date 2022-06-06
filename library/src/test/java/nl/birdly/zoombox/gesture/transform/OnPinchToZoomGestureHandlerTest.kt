package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import nl.birdly.zoombox.Zoom
import org.junit.Test
import java.lang.AssertionError
import kotlin.math.absoluteValue

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

        assertEqualsRounded(Zoom(), updatedZoom)
    }

    @Test
    fun `Zoom in top left corner, stays in top left corner`() {
        var updatedZoom: Zoom? = null

        invoke(
            centroid = Offset(0f, 0f),
            gestureZoom = 2f
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(Zoom(scale = 2f, offset = Offset(0f, 0f)), updatedZoom)
    }

    @Test
    fun `Zoom in top right corner, stays in top right corner, 2,0`() {
        var updatedZoom: Zoom? = null

        invoke(
            centroid = Offset(100f, 0f),
            gestureZoom = 2f
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(Zoom(scale = 2f, offset = Offset(100f, 0f)), updatedZoom)
    }

    @Test
    fun `Zoom in top right corner stays in top right corner, 1,1`() {
        var updatedZoom: Zoom? = null

        invoke(
            centroid = Offset(100f, 0f),
            gestureZoom = 1.1f
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(
            Zoom(scale = 1.1f, offset = Offset(10f, 0f)),
            updatedZoom
        )
    }

    @Test
    fun `Zoom in top right corner stays in top right corner, 1,1 to 1,2`() {
        var updatedZoom: Zoom? = null

        invoke(
            zoom = Zoom(scale = 1.1f, offset = Offset(10f, 0f)),
            centroid = Offset(100f, 0f),
            gestureZoom = 1.1f
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(
            Zoom(scale = 1.21f, offset = Offset(21f, 0f)),
            updatedZoom
        )
    }

    @Test
    fun `Limit zoom by min zoomRange`() {
        var updatedZoom: Zoom? = null

        invoke(
            centroid = Offset.Zero,
            zoomRange = 1f..2f,
            gestureZoom = 0.5f
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(
            Zoom(),
            updatedZoom
        )
    }

    @Test
    fun `Limit zoom by max zoomRange`() {
        var updatedZoom: Zoom? = null

        invoke(
            centroid = Offset.Zero,
            zoomRange = 1f..2f,
            gestureZoom = 3f
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(
            Zoom(scale = 2f),
            updatedZoom
        )
    }

    @Test
    fun `Zoom out from zoomed in should work`() {
        var updatedZoom: Zoom? = null

        invoke(
            zoom = Zoom(scale = 2f),
            centroid = Offset.Zero,
            zoomRange = 1f..2f,
            gestureZoom = 0.5f
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(
            Zoom(scale = 1f),
            updatedZoom
        )
    }

    @Test
    fun `Panning should work`() {
        var updatedZoom: Zoom? = null

        invoke(
            zoom = Zoom(scale = 2f, offset = Offset(50f, 50f)),
            centroid = Offset(50f, 50f),
            pan = Offset(-10f, 10f)
        ) {
            updatedZoom = it
        }

        assertEqualsRounded(
            Zoom(scale = 2f, offset = Offset(70f, 30f)),
            updatedZoom
        )
    }

    private fun assertEqualsRounded(expected: Zoom, actual: Zoom?, accuracy: Double = 0.0001) {
        var isNotEqual = false
        if (actual == null) throw AssertionError("$expected is not equal to $actual")
        if ((expected.scale - actual.scale).absoluteValue > accuracy) isNotEqual = true
        if ((expected.offset.x - actual.offset.x).absoluteValue > accuracy) isNotEqual = true
        if ((expected.offset.y - actual.offset.y).absoluteValue > accuracy) isNotEqual = true
        if ((expected.angle - actual.angle).absoluteValue > accuracy) isNotEqual = true
        if (isNotEqual) throw AssertionError("$expected is not equal to $actual")
    }

    fun invoke(
        centroid: Offset,
        zoomRange: ClosedFloatingPointRange<Float> = 1.0f..2.0f,
        pan: Offset = Offset(0f, 0f),
        zoom: Zoom = Zoom(),
        gestureZoom: Float = 1f,
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