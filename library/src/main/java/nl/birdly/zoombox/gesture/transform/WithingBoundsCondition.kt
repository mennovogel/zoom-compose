package nl.birdly.zoombox.gesture.transform

import android.util.Log
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.Calculator

class WithingBoundsCondition : TouchCondition {

    override fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ): Boolean {
        val zoomState = zoomStateProvider()

        val translationX = pointerEvent.changes.first().previousPosition.x -
                pointerEvent.changes.first().position.x
        val maxTranslationX = Calculator.calculateMaxTranslation(
            zoomState.scale,
            pointerInputScope.size.width
        )
        zoomState.offset.x

        Log.d("Menno", "state: $zoomState")
        Log.d("Menno", "invoke: offsetX: ${zoomState.offset.x}, translationX: $translationX, " +
                "maxTranslationX: $maxTranslationX")

        val returnValue = pointerEvent.changes.size > 1 ||
                zoomState.offset.x + translationX in 0.0..maxTranslationX.toDouble()
        Log.d("Menno", "returnValue: $returnValue")
        return returnValue
    }
}