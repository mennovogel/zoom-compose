package nl.birdly.zoombox

import androidx.compose.foundation.gestures.TransformableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoombox.util.animateZoomBy

class ResetOnCanceledHandler : (CoroutineScope, TransformableState, Zoom, (Zoom) -> Unit) -> Unit {
    override fun invoke(
        scope: CoroutineScope,
        state: TransformableState,
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