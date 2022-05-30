package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoombox.Zoom
import nl.birdly.zoombox.util.animateZoomBy

class ResetToOriginalPositionOnCancelledBehavior : OnCancelledBehavior {

    override fun onCancelled(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        zoom: Zoom,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        scope.launch {
            state.animateZoomBy(
                zoom,
                Zoom(),
                onZoomUpdated = onZoomUpdated
            )
        }
    }
}