package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.Calculator
import nl.birdly.zoombox.util.animateZoomBy
import nl.birdly.zoombox.util.minMax

class KeepWithinBoundsOnCancelledBehavior : OnCancelledBehavior {

    override fun onCancelled(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        childImageBounds: Rect,
        zoomState: ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        scope.launch {
            val translationXWithinBounds = Calculator.keepTranslationWithinBounds(
                -zoomState.offset.x,
                zoomState.scale,
                pointerInputScope.size.width,
                childImageBounds.width.toInt()
            )
            val translationYWithinBounds = Calculator.keepTranslationWithinBounds(
                -zoomState.offset.y,
                zoomState.scale,
                pointerInputScope.size.height,
                childImageBounds.height.toInt()
            )

            val maxTranslationX = Calculator.calculateMaxTranslation(
                zoomState.scale,
                pointerInputScope.size.width
            )
            val translationX = minMax(-maxTranslationX, 0f, -zoomState.offset.x)
            val maxTranslationY = Calculator.calculateMaxTranslation(
                zoomState.scale,
                pointerInputScope.size.height
            )
            val translationY= minMax(-maxTranslationY, 0f, -zoomState.offset.y)

            state.animateZoomBy(
                zoomState,
                zoomState.copy(
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