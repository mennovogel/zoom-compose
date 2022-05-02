package nl.birdly.zoom.gesture.transform

import androidx.compose.ui.input.pointer.PointerEvent

class OnDoubleTouchCondition : (PointerEvent) -> Boolean {

    override fun invoke(pointerEvent: PointerEvent) = pointerEvent.changes.size > 1

}
