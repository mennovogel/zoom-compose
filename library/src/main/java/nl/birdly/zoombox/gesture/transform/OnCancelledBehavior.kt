package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import nl.birdly.zoombox.Zoom

interface OnCancelledBehavior {

    fun onCancelled(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        zoom: Zoom,
        onZoomUpdated: (Zoom) -> Unit
    )
}