package nl.birdly.zoom

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoom.util.Calculator
import nl.birdly.zoom.util.animateZoomBy
import nl.birdly.zoom.util.detectTransformGestures
import nl.birdly.zoom.util.minMax
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Zooming is based on the example on https://developer.android.google.cn/reference/kotlin/androidx/compose/foundation/gestures/package-summary#(androidx.compose.ui.input.pointer.PointerInputScope).detectTransformGestures(kotlin.Boolean,kotlin.Function4)
 */
@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    minZoom: Float = 1f,
    maxZoom: Float = 2f,
    zoomingZIndex: Float = 1f,
    defaultZIndex: Float = 0f,
    rotation: Boolean = false,
    onCanceledHandler: (
        CoroutineScope,
        TransformableState,
        Zoom,
        (Zoom) -> Unit
    ) -> Unit = ResetOnCanceledHandler(),
    content: @Composable () -> Unit
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
                detectTapGestures(
                    onDoubleTap = {
                        val futureScale = if (zoom.scale >= maxZoom - 0.1f) minZoom else maxZoom

                        Log.d("Menno", "Zoomable: futureScale: $futureScale")
                        Log.d("Menno", "Zoomable: touchX: ${it.x}, touchY: ${it.y}")

                        scope.launch {
                            state.animateZoomBy(
                                zoom,
                                futureScale,
                                it,
                                size
                            ) { newZoom ->
                                zoom = newZoom
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures(
                    onCondition = { pointerEvent ->
                        pointerEvent.changes.size > 1
                    },
                    onCanceled = {
                        onCanceledHandler(scope, state, zoom) {
                            zoom = it
                        }
                    },
                    onGesture = { centroid: Offset,
                                  pan: Offset,
                                  gestureZoom: Float,
                                  gestureRotate: Float ->
                        val oldScale = zoom.scale
                        val newScale = minMax(minZoom, maxZoom, zoom.scale * gestureZoom)
                        val newOffset =
                            (zoom.offset + centroid / oldScale).rotateBy(gestureRotate) -
                                    (centroid / newScale + pan * zoom.scale)
                        val newAngle = if (rotation) {
                            zoom.angle + gestureRotate
                        } else {
                            zoom.angle
                        }
                        zoom = Zoom(
                            newScale,
                            newAngle,
                            newOffset
                        )
                    }
                )
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