package nl.birdly.zoom

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import nl.birdly.zoom.gesture.tap.TapHandler
import nl.birdly.zoom.gesture.transform.TransformGestureHandler
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoom.util.detectTransformGestures
import nl.birdly.zoom.util.minMax
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Zooming is based on the example on https://developer.android.google.cn/reference/kotlin/androidx/compose/foundation/gestures/package-summary#(androidx.compose.ui.input.pointer.PointerInputScope).detectTransformGestures(kotlin.Boolean,kotlin.Function4)
 */
@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    zoomRange: ClosedFloatingPointRange<Float> = 1f..2f,
    zoomingZIndex: Float = 1f,
    defaultZIndex: Float = 0f,
    rotation: Boolean = false,
    tapHandler: TapHandler = TapHandler(),
    transformGestureHandler: TransformGestureHandler = TransformGestureHandler(),
    content: @Composable BoxScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    var zoom: Zoom by remember { mutableStateOf(Zoom())}
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        zoom = zoom.copy(
            scale = zoom.scale * zoomChange,
            angle = zoom.angle + rotationChange,
            offset = zoom.offset + offsetChange
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = zoom.scale,
                scaleY = zoom.scale,
                translationX = -zoom.offset.x,
                translationY = -zoom.offset.y,
                rotationZ = zoom.angle,
                transformOrigin = TransformOrigin(0f, 0f)
            )
            .zIndex(if (zoom.scale > 1.0f) zoomingZIndex else defaultZIndex)
            .pointerInput(Unit) {
                tapHandler(scope,
                    this,
                    state,
                    zoomRange,
                    zoomProvider = { zoom }
                ) { newZoom ->
                    zoom = newZoom
                }
            }
            .pointerInput(Unit) {
                transformGestureHandler.invoke(
                    scope,
                    this,
                    state,
                    zoomRange,
                    rotation,
                    zoomProvider = { zoom }
                ) { newZoom ->
                    zoom = newZoom
                }
            },
        content = { content() }
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
    ZoomTheme {
        val bitmap: Bitmap = with(LocalContext.current.assets.open("Dolphin.jpg")){
            BitmapFactory.decodeStream(this)
        }

        Zoomable {
            Image(bitmap.asImageBitmap(), contentDescription = "")
        }
    }
}