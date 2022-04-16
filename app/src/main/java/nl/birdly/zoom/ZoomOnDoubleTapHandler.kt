package nl.birdly.zoom

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoom.util.animateZoomBy

class ZoomOnDoubleTapHandler : OnDoubleTapHandler {

    override suspend fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        zoomProvider: () -> Zoom,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        pointerInputScope.detectTapGestures(
            onDoubleTap = { offset ->
                val zoom = zoomProvider()
                val futureScale = if (zoom.scale >= zoomRange.endInclusive - 0.1f) {
                    zoomRange.start
                } else {
                    zoomRange.endInclusive
                }

                scope.launch {
                    state.animateZoomBy(
                        zoom,
                        futureScale,
                        offset,
                        pointerInputScope.size,
                        onZoomUpdated = onZoomUpdated
                    )
                }
            }
        )
    }
}