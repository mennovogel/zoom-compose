package nl.birdly.zoombox.gesture.transform

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import nl.birdly.zoombox.Zoom
import nl.birdly.zoombox.util.detectTransformGestures

class TransformGestureHandler(
    private val panZoomLock: Boolean = false,
    private val onCancelledBehavior: OnCancelledBehavior =
        ResetToOriginalPositionOnCancelledBehavior(),
    private val onCondition: (pointerEvent: PointerEvent) -> Boolean = { true },
    private val onGesture: (Offset, ClosedFloatingPointRange<Float>, Offset, Zoom, Float, Float, Boolean, (Zoom) -> Unit) -> Unit
    = OnPinchToZoomGestureHandler()
) {

    @RequiresApi(Build.VERSION_CODES.Q)
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
                onCancelledBehavior.onCancelled(
                    scope,
                    state,
                    pointerInputScope,
                    Rect(0f, 1000f, 1080f, 1720f),
                    zoomProvider(),
                    onZoomUpdated
                )
            },
            onGesture = { centroid: Offset, pan: Offset, gestureZoom: Float, gestureRotation: Float ->
                onGesture(
                    centroid,
                    zoomRange,
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
