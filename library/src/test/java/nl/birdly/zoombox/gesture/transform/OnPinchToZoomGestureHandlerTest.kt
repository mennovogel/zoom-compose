package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import nl.birdly.zoombox.ZoomState
import org.junit.Test
import java.lang.AssertionError
import kotlin.math.absoluteValue

class OnPinchToZoomGestureHandlerTest {

    private val handler = OnPinchToZoomGestureHandler()

    @Test
    fun `Touch only does not zoom`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset(50f, 50f)
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(ZoomState(), updatedZoomState)
    }

    @Test
    fun `Zoom in top left corner, stays in top left corner`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset(0f, 0f),
            gestureZoom = 2f
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(ZoomState(scale = 2f, offset = Offset(0f, 0f)), updatedZoomState)
    }

    @Test
    fun `Zoom in top right corner, stays in top right corner, 2,0`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset(100f, 0f),
            gestureZoom = 2f
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(ZoomState(scale = 2f, offset = Offset(100f, 0f)), updatedZoomState)
    }

    @Test
    fun `Zoom in top right corner stays in top right corner, 1,1`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset(100f, 0f),
            gestureZoom = 1.1f
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(
            ZoomState(scale = 1.1f, offset = Offset(10f, 0f)),
            updatedZoomState
        )
    }

    @Test
    fun `Zoom in top right corner stays in top right corner, 1,1 to 1,2`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset(100f, 0f),
            zoomState = ZoomState(scale = 1.1f, offset = Offset(10f, 0f)),
            gestureZoom = 1.1f
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(
            ZoomState(scale = 1.21f, offset = Offset(21f, 0f)),
            updatedZoomState
        )
    }

    @Test
    fun `Limit zoom by min zoomRange`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset.Zero,
            zoomRange = 1f..2f,
            gestureZoom = 0.5f
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(
            ZoomState(),
            updatedZoomState
        )
    }

    @Test
    fun `Limit zoom by max zoomRange`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset.Zero,
            zoomRange = 1f..2f,
            gestureZoom = 3f
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(
            ZoomState(scale = 2f),
            updatedZoomState
        )
    }

    @Test
    fun `Zoom out from zoomed in should work`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset.Zero,
            zoomRange = 1f..2f,
            zoomState = ZoomState(scale = 2f),
            gestureZoom = 0.5f
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(
            ZoomState(scale = 1f),
            updatedZoomState
        )
    }

    @Test
    fun `Panning should work`() {
        var updatedZoomState: ZoomState? = null

        invoke(
            centroid = Offset(50f, 50f),
            pan = Offset(-10f, 10f),
            zoomState = ZoomState(scale = 2f, offset = Offset(50f, 50f))
        ) {
            updatedZoomState = it
        }

        assertEqualsRounded(
            ZoomState(scale = 2f, offset = Offset(70f, 30f)),
            updatedZoomState
        )
    }

    private fun assertEqualsRounded(expected: ZoomState, actual: ZoomState?, accuracy: Double = 0.0001) {
        var isNotEqual = false
        if (actual == null) throw AssertionError("$expected is not equal to $actual")
        if ((expected.scale - actual.scale).absoluteValue > accuracy) isNotEqual = true
        if ((expected.offset.x - actual.offset.x).absoluteValue > accuracy) isNotEqual = true
        if ((expected.offset.y - actual.offset.y).absoluteValue > accuracy) isNotEqual = true
        if (isNotEqual) throw AssertionError("$expected is not equal to $actual")
    }

    fun invoke(
        centroid: Offset,
        zoomRange: ClosedFloatingPointRange<Float> = 1.0f..2.0f,
        pan: Offset = Offset(0f, 0f),
        zoomState: ZoomState = ZoomState(),
        gestureZoom: Float = 1f,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        handler.invoke(
            centroid,
            zoomRange,
            pan,
            zoomState,
            gestureZoom,
            onZoomUpdated
        )
    }
}