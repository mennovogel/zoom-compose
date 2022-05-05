package nl.birdly.zoombox.gesture.transform

import android.util.Log
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoombox.Zoom
import nl.birdly.zoombox.util.Calculator
import nl.birdly.zoombox.util.animateZoomBy
import nl.birdly.zoombox.util.minMax

class ResetOnCanceledHandler(
    private val behavior: Behavior = Behavior.ResetToOriginalPosition
) : (CoroutineScope, TransformableState, PointerInputScope, Zoom, (Zoom) -> Unit) -> Unit {

    override fun invoke(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        zoom: Zoom,
        onZoomUpdated: (Zoom) -> Unit
    ) {
        scope.launch {
            when (behavior) {
                is Behavior.ResetToOriginalPosition -> {
                    state.animateZoomBy(
                        zoom,
                        Zoom(),
                        onZoomUpdated = onZoomUpdated
                    )
                }
                Behavior.KeepWithinBounds -> {
                    val xOffset = Calculator.calculateMaxTranslation(
                        zoom.scale,
                        pointerInputScope.size.width
                    )
                    // TODO: Finish this behavior, calculation should be the same for x and y.
                    Log.d("Menno", "xOffset: $xOffset, zoom.offset.x: ${zoom.offset.x}, minMax: ${minMax(-xOffset, xOffset, zoom.offset.x)}")
                    state.animateZoomBy(
                        zoom,
                        zoom.copy(
                            offset = Offset(
                                minMax(-xOffset, 0f, -zoom.offset.x),
                                -Calculator.calculateMaxTranslation(
                                    zoom.scale,
                                    pointerInputScope.size.height
                                ) / 2f
                            )
                        ),
                        onZoomUpdated = onZoomUpdated
                    )
                }
            }
        }
    }

    sealed class Behavior {
        object ResetToOriginalPosition : Behavior()
        object KeepWithinBounds : Behavior()
    }
}