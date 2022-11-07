package nl.birdly.zoombox

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.zIndex
import nl.birdly.zoombox.gesture.tap.TapHandler
import nl.birdly.zoombox.gesture.transform.TransformGestureHandler

@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    zoomState: MutableZoomState = rememberMutableZoomState(),
    zoomRange: ClosedFloatingPointRange<Float> = 1f..3f,
    zoomingZIndex: Float = 1f,
    defaultZIndex: Float = 0f,
    tapHandler: TapHandler = TapHandler(),
    transformGestureHandler: TransformGestureHandler = TransformGestureHandler(),
    content: @Composable (ZoomState) -> Unit
) {
    val scope = rememberCoroutineScope()
    val immutableZoomState = zoomState.value
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        zoomState.value = immutableZoomState.copy(
            scale = immutableZoomState.scale * zoomChange,
            offset = immutableZoomState.offset + offsetChange
        )
    }

    val isMoving = immutableZoomState.scale != 1.0f ||
            immutableZoomState.offset != Offset(0f, 0f)
    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = immutableZoomState.scale,
                scaleY = immutableZoomState.scale,
                translationX = -immutableZoomState.offset.x,
                translationY = -immutableZoomState.offset.y,
                transformOrigin = TransformOrigin(0f, 0f)
            )
            .zIndex(if (isMoving) zoomingZIndex else defaultZIndex)
            .pointerInput(Unit) {
                tapHandler(scope,
                    this,
                    state,
                    zoomRange,
                    zoomStateProvider = { zoomState.value }
                ) { newZoom ->
                    zoomState.value = newZoom
                }
            }
            .pointerInput(Unit) {
                transformGestureHandler.invoke(
                    scope,
                    this,
                    state,
                    zoomRange,
                    zoomStateProvider = { zoomState.value }
                ) { newZoom ->
                    zoomState.value = newZoom
                }
            },
        content = {
            Box(Modifier
                .align(Alignment.Center)
                .onGloballyPositioned { layoutCoordinates ->
                    val positionInParent = layoutCoordinates.positionInParent()
                    val childRect = Rect(
                        positionInParent.x,
                        positionInParent.y,
                        positionInParent.x + layoutCoordinates.size.width,
                        positionInParent.y + layoutCoordinates.size.height
                    )
                    if (immutableZoomState.childRect != childRect) {
                        zoomState.value = immutableZoomState.copy(
                            childRect = childRect
                        )
                    }
            }) {
                content(immutableZoomState)
            }
        }
    )
}