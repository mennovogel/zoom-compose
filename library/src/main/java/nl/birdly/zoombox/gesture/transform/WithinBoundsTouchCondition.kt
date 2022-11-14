package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.Calculator

class WithinBoundsTouchCondition : TouchCondition {

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

        return pointerEvent.changes.size > 1 ||
                zoomState.offset.x + translationX in 0.0..maxTranslationX.toDouble()
    }
}