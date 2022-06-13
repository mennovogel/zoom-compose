package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
        childImageBounds: Rect,
        zoom: Zoom,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        scope.launch {
            val translationXWithinBounds = Calculator.keepTranslationWithinBounds(
                -zoom.offset.x,
                zoom.scale,
                pointerInputScope.size.width,
                childImageBounds.width.toInt()
            )
            val translationYWithinBounds = Calculator.keepTranslationWithinBounds(
                -zoom.offset.y,
                zoom.scale,
                pointerInputScope.size.height,
                childImageBounds.height.toInt()
            )

            val maxTranslationX = Calculator.calculateMaxTranslation(
                zoom.scale,
                pointerInputScope.size.width
            )
            val translationX = minMax(-maxTranslationX, 0f, -zoom.offset.x)
            val maxTranslationY = Calculator.calculateMaxTranslation(
                zoom.scale,
                pointerInputScope.size.height
            )
            val translationY= minMax(-maxTranslationY, 0f, -zoom.offset.y)

            state.animateZoomBy(
                zoom,
                zoom.copy(
                    offset = Offset(
                        translationXWithinBounds,
                        translationYWithinBounds
                    )
                ),
                onZoomUpdated = onZoomUpdated
            )
        }
    }
}