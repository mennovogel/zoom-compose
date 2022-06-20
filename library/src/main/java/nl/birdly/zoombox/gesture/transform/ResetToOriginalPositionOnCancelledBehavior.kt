package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.animateZoomBy

class ResetToOriginalPositionOnCancelledBehavior : OnCancelledBehavior {

    override fun onCancelled(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        childImageBounds: Rect,
        zoomState: ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        scope.launch {
            state.animateZoomBy(
                zoomState,
                ZoomState(),
                onZoomUpdated = onZoomUpdated
            )
        }
    }
}