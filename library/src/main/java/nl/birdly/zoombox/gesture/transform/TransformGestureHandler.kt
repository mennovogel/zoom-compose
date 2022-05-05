package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import nl.birdly.zoombox.util.detectTransformGestures

class TransformGestureHandler(
    private val panZoomLock: Boolean = false,
    private val onCanceled: (
        CoroutineScope,
        TransformableState,
        nl.birdly.zoombox.Zoom,
        (nl.birdly.zoombox.Zoom) -> Unit
    ) -> Unit = { _: CoroutineScope, _: TransformableState, _: nl.birdly.zoombox.Zoom, _: (nl.birdly.zoombox.Zoom) -> Unit -> },
    private val onCondition: (pointerEvent: PointerEvent) -> Boolean = { true },
    private val onGesture: (Offset, ClosedFloatingPointRange<Float>, TransformableState, Offset, nl.birdly.zoombox.Zoom, Float, Float, Boolean, (nl.birdly.zoombox.Zoom) -> Unit) -> Unit
    = OnPinchToZoomGestureHandler()
) {

    suspend operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        rotation: Boolean,
        zoomProvider: () -> nl.birdly.zoombox.Zoom,
        onZoomUpdated: (nl.birdly.zoombox.Zoom) -> Unit
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
