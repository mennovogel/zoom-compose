package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoombox.Zoom
import nl.birdly.zoombox.util.Calculator
import nl.birdly.zoombox.util.animateZoomBy
import nl.birdly.zoombox.util.minMax

class KeepWithinBoundsOnCancelledBehavior : OnCancelledBehavior {

    override fun onCancelled(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        zoom: Zoom,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        scope.launch {
            val maxTranslationX = Calculator.calculateMaxTranslation(
                zoom.scale,
                pointerInputScope.size.width
            )
            val maxTranslationY = Calculator.calculateMaxTranslation(
                zoom.scale,
                pointerInputScope.size.height
            )
            state.animateZoomBy(
                zoom,
                zoom.copy(
                    offset = Offset(
                        minMax(-maxTranslationX, 0f, -zoom.offset.x),
                        minMax(-maxTranslationY, 0f, -zoom.offset.y)
                    )
                ),
                onZoomUpdated = onZoomUpdated
            )
        }
    }
}