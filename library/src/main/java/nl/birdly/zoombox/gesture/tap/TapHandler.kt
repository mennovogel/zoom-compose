package nl.birdly.zoombox.gesture.tap

import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope

class TapHandler(
    private val onDoubleTap: OnDoubleTapHandler? = ZoomOnDoubleTapHandler(),
    private val onLongPress: ((Offset) -> Unit)? = null,
    private val onPress: suspend PressGestureScope.(Offset) -> Unit = { },
    private val onTap: ((Offset) -> Unit)? = null
) {

    suspend operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        zoomProvider: () -> nl.birdly.zoombox.Zoom,
        onZoomUpdated: (nl.birdly.zoombox.Zoom) -> Unit
    ) {
        pointerInputScope.detectTapGestures(
            onDoubleTap = onDoubleTap?.let { onDoubleTap ->
                { offset: Offset ->
                    onDoubleTap(
                        scope,
                        pointerInputScope,
                        state,
                        zoomRange,
                        offset,
                        zoomProvider,
                        onZoomUpdated
                    )
                }
            },
            onLongPress = onLongPress,
            onPress = onPress,
            onTap = onTap
        )
    }
}