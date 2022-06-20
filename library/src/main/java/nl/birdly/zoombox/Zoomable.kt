package nl.birdly.zoombox

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Zooming is based on the example on https://developer.android.google.cn/reference/kotlin/androidx/compose/foundation/gestures/package-summary#(androidx.compose.ui.input.pointer.PointerInputScope).detectTransformGestures(kotlin.Boolean,kotlin.Function4)
 *
 * TODO:
 * - Support flinging when zoomed in.
 */
@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    zoomRange: ClosedFloatingPointRange<Float> = 1f..3f,
    zoomingZIndex: Float = 1f,
    defaultZIndex: Float = 0f,
    rotation: Boolean = false,
    tapHandler: TapHandler = TapHandler(),
    transformGestureHandler: TransformGestureHandler = TransformGestureHandler(),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    var zoomState: ZoomState by remember { mutableStateOf(ZoomState())}
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        zoomState = zoomState.copy(
            scale = zoomState.scale * zoomChange,
            angle = zoomState.angle + rotationChange,
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
                rotationZ = zoomState.angle,
                transformOrigin = TransformOrigin(0f, 0f)
            )
            .zIndex(if (zoomState.scale > 1.0f) zoomingZIndex else defaultZIndex)
            .pointerInput(Unit) {
                tapHandler(scope,
                    this,
                    state,
                    zoomRange,
                    zoomStateProvider = { zoomState }
                ) { newZoom ->
                    zoomState = newZoom
                }
            }
            .pointerInput(Unit) {
                transformGestureHandler.invoke(
                    scope,
                    this,
                    state,
                    zoomRange,
                    rotation,
                    zoomStateProvider = { zoomState }
                ) { newZoom ->
                    zoomState = newZoom
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
                        zoomState = zoomState.copy(
                            childRect = childRect
                        )
                    }
            }) {
                content()
            }
        }
    )
}

fun Offset.rotateBy(angle: Float): Offset {
    val angleInRadians = angle * PI / 180
    return Offset(
        (x * cos(angleInRadians) - y * sin(angleInRadians)).toFloat(),
        (x * sin(angleInRadians) + y * cos(angleInRadians)).toFloat()
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