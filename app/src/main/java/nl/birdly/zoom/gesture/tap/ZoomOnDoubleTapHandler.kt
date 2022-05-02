package nl.birdly.zoom.gesture.tap

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoom.Zoom
import nl.birdly.zoom.util.animateZoomBy

class ZoomOnDoubleTapHandler : OnDoubleTapHandler {

    override fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        offset: Offset,
        zoomProvider: () -> Zoom,
        onZoomUpdated: (Zoom) -> Unit
    ) {
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
}