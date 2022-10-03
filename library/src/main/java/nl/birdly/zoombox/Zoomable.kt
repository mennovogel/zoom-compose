package nl.birdly.zoombox

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import nl.birdly.zoombox.gesture.tap.TapHandler
import nl.birdly.zoombox.gesture.transform.TransformGestureHandler

@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    mutableZoomState: MutableZoomState = rememberMutableZoomState(),
    zoomRange: ClosedFloatingPointRange<Float> = 1f..3f,
    zoomingZIndex: Float = 1f,
    defaultZIndex: Float = 0f,
    tapHandler: TapHandler = TapHandler(),
    transformGestureHandler: TransformGestureHandler = TransformGestureHandler(),
    content: @Composable (ZoomState) -> Unit
) {
    val scope = rememberCoroutineScope()
    val zoomState = mutableZoomState.value
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        mutableZoomState.value = zoomState.copy(
            scale = zoomState.scale * zoomChange,
            offset = zoomState.offset + offsetChange
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = zoomState.scale,
                scaleY = zoomState.scale,
                translationX = -zoomState.offset.x,
                translationY = -zoomState.offset.y,
                transformOrigin = TransformOrigin(0f, 0f)
            )
            .zIndex(if (zoomState.scale > 1.0f) zoomingZIndex else defaultZIndex)
            .pointerInput(Unit) {
                tapHandler(scope,
                    this,
                    state,
                    zoomRange,
                    zoomStateProvider = { mutableZoomState.value }
                ) { newZoom ->
                    mutableZoomState.value = newZoom
                }
            }
            .pointerInput(Unit) {
                transformGestureHandler.invoke(
                    scope,
                    this,
                    state,
                    zoomRange,
                    zoomStateProvider = { mutableZoomState.value }
                ) { newZoom ->
                    mutableZoomState.value = newZoom
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
                    if (zoomState.childRect != childRect) {
                        mutableZoomState.value = zoomState.copy(
                            childRect = childRect
                        )
                    }
            }) {
                content(zoomState)
            }
        }
    )
}

@Preview
@Composable
fun ZoomablePreview() {
    MaterialTheme {
        val bitmap: Bitmap = with(LocalContext.current.assets.open("Dolphin.jpg")){
            BitmapFactory.decodeStream(this)
        }

        Zoomable {
            Image(bitmap.asImageBitmap(), contentDescription = "")
        }
    }
}