package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import nl.birdly.zoombox.ZoomState

interface TouchCondition {

    operator fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ): Boolean
}