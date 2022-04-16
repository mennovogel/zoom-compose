package nl.birdly.zoom

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope

interface OnDoubleTapHandler {

    suspend operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        zoomProvider: () -> Zoom,
        onZoomUpdated: (Zoom) -> Unit
    )
}