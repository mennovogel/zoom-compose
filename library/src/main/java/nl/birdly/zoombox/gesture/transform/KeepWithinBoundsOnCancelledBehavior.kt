package nl.birdly.zoombox.gesture.transform

import android.util.Log
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
            val translationYWithinBounds = Calculator.keepTranslationWithinBounds(
                -zoom.offset.y,
                zoom.scale,
                pointerInputScope.size.height,
                childImageBounds.height.toInt()
            )
            Log.d("Menno", "keepTranslationWithinBounds: translation=${-zoom.offset.y}, " +
                    "scale=${zoom.scale}, " +
                    "childZoomViewSize=${childImageBounds.height.toInt()}," +
                    "parentZoomViewSize=${pointerInputScope.size.height}," +
                    "translationYWithinBounds=$translationYWithinBounds")

            val maxTranslationX = Calculator.calculateMaxTranslation(
                zoom.scale,
                pointerInputScope.size.width
            )
            val maxTranslationY = Calculator.calculateMaxTranslation(
                zoom.scale,
                pointerInputScope.size.height
            )
            Log.d("Menno", "onCancelled: translation=${-zoom.offset.y}, " +
                    "translationYWithinBounds=$translationYWithinBounds")
            state.animateZoomBy(
                zoom,
                zoom.copy(
                    offset = Offset(
                        minMax(-maxTranslationX, 0f, -zoom.offset.x),
//                        minMax(-maxTranslationY, 0f, -zoom.offset.y)
                        translationYWithinBounds
//                        -600f
                    )
                ),
                onZoomUpdated = onZoomUpdated
            )
        }
    }
}