package nl.birdly.zoombox.gesture.tap

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoombox.util.animateZoomBy

class ZoomOnDoubleTapHandler : OnDoubleTapHandler {

    override fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        offset: Offset,
        zoomProvider: () -> nl.birdly.zoombox.Zoom,
        onZoomUpdated: (nl.birdly.zoombox.Zoom) -> Unit
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