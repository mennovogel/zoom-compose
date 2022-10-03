package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.detectTransformGestures

class TransformGestureHandler(
    private val panZoomLock: Boolean = false,
    private val onCancelledBehavior: OnCancelledBehavior =
        ResetToOriginalPositionOnCancelledBehavior(),
    private val onCondition: (pointerEvent: PointerEvent) -> Boolean = { true },
    private val onGesture: (Offset, ClosedFloatingPointRange<Float>, Offset, ZoomState, Float, (ZoomState) -> Unit) -> Unit
    = OnPinchToZoomGestureHandler()
) {

    suspend operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        zoomStateProvider: () -> ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        val zoom = zoomStateProvider()
        zoom.childRect ?: return
        pointerInputScope.detectTransformGestures(
            onCondition = onCondition,
            panZoomLock = panZoomLock,
            onCancelled = {
                onCancelledBehavior.onCancelled(
                    scope,
                    state,
                    pointerInputScope,
                    zoom.childRect,
                    zoomStateProvider(),
                    onZoomUpdated
                )
            },
            onGesture = { centroid: Offset, pan: Offset, gestureZoom: Float, gestureRotation: Float ->
                onGesture(
                    centroid,
                    zoomRange,
                    pan,
                    zoomStateProvider(),
                    gestureZoom,
                    onZoomUpdated
                )
            }
        )
    }
}
