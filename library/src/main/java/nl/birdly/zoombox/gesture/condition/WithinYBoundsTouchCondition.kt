package nl.birdly.zoombox.gesture.condition

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.util.Calculator

class WithinYBoundsTouchCondition : TouchCondition {

    override fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ): Boolean {
        val zoomState = zoomStateProvider()

        val translationY = pointerEvent.changes.first().previousPosition.y -
                pointerEvent.changes.first().position.y
        val maxTranslationY = Calculator.calculateMaxTranslation(
            zoomState.scale,
            pointerInputScope.size.height
        )
        zoomState.offset.y

        return pointerEvent.changes.size > 1 ||
                zoomState.offset.y + translationY in 0.0..maxTranslationY.toDouble()
    }
}