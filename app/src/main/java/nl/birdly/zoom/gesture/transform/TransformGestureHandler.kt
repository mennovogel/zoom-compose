package nl.birdly.zoom.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import nl.birdly.zoom.Zoom
import nl.birdly.zoom.util.detectTransformGestures

class TransformGestureHandler(
    private val panZoomLock: Boolean = false,
    private val onCanceled: (
        CoroutineScope,
        TransformableState,
        Zoom,
        (Zoom) -> Unit
    ) -> Unit = { _: CoroutineScope, _: TransformableState, _: Zoom, _: (Zoom) -> Unit -> },
    private val onCondition: (pointerEvent: PointerEvent) -> Boolean = { true },
    private val onGesture: (Offset, ClosedFloatingPointRange<Float>, TransformableState, Offset, Zoom, Float, Float, Boolean, (Zoom) -> Unit) -> Unit
    = OnPinchToZoomGestureHandler()
) {

    suspend operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        rotation: Boolean,
        zoomProvider: () -> Zoom,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        pointerInputScope.detectTransformGestures(
            onCondition = onCondition,
            panZoomLock = panZoomLock,
            onCanceled = {
                onCanceled(scope, state, zoomProvider(), onZoomUpdated)
            },
            onGesture = { centroid: Offset, pan: Offset, gestureZoom: Float, gestureRotation: Float ->
                onGesture(
                    centroid,
                    zoomRange,
                    state,
                    pan,
                    zoomProvider(),
                    gestureZoom,
                    gestureRotation,
                    rotation,
                    onZoomUpdated
                )
            }
        )
    }
}
