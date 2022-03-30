package nl.birdly.zoom

import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Zoomable2() {
    val scope = rememberCoroutineScope()

    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var imageCenter by remember { mutableStateOf(Offset.Zero) }
    var transformOffset by remember { mutableStateOf(Offset.Zero) }


    /*Box(
        Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale != 1f) {
                            scope.launch {
                                state.animateZoomBy(1 / scale)
                            }
                            offset = Offset.Zero
                            rotation = 0f
                        } else {
                            scope.launch {
                                state.animateZoomBy(2f)
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                val panZoomLock = true
                forEachGesture {
                    awaitPointerEventScope {
                        var transformRotation = 0f
                        var zoom = 1f
                        var pan = Offset.Zero
                        var pastTouchSlop = false
                        val touchSlop = viewConfiguration.touchSlop
                        var lockedToPanZoom = false
                        var drag: PointerInputChange?
                        var overSlop = Offset.Zero

                        val down = awaitFirstDown(requireUnconsumed = false)


                        var transformEventCounter = 0
                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.any { it.positionChangeConsumed() }
                            var relevant = true
                            if (event.changes.size > 1) {
                                if (!canceled) {
                                    val zoomChange = event.calculateZoom()
                                    val rotationChange = event.calculateRotation()
                                    val panChange = event.calculatePan()

                                    if (!pastTouchSlop) {
                                        zoom *= zoomChange
                                        transformRotation += rotationChange
                                        pan += panChange

                                        val centroidSize =
                                            event.calculateCentroidSize(useCurrent = false)
                                        val zoomMotion = abs(1 - zoom) * centroidSize
                                        val rotationMotion =
                                            abs(transformRotation * PI.toFloat() * centroidSize / 180f)
                                        val panMotion = pan.getDistance()

                                        if (zoomMotion > touchSlop ||
                                            rotationMotion > touchSlop ||
                                            panMotion > touchSlop
                                        ) {
                                            pastTouchSlop = true
                                            lockedToPanZoom =
                                                panZoomLock && rotationMotion < touchSlop
                                        }
                                    }

                                    if (pastTouchSlop) {
                                        val eventCentroid = event.calculateCentroid(useCurrent = false)
                                        val effectiveRotation =
                                            if (lockedToPanZoom) 0f else rotationChange
                                        if (effectiveRotation != 0f ||
                                            zoomChange != 1f ||
                                            panChange != Offset.Zero
                                        ) {
                                            onTransformGesture(
                                                eventCentroid,
                                                panChange,
                                                zoomChange,
                                                effectiveRotation
                                            )
                                        }
                                        event.changes.fastForEach {
                                            if (it.positionChanged()) {
                                                it.consumeAllChanges()
                                            }
                                        }
                                    }
                                }
                            } else if (transformEventCounter > 3) relevant = false
                            transformEventCounter++
                        } while (!canceled && event.changes.fastAny { it.pressed } && relevant)

                        do {
                            val event = awaitPointerEvent()
                            drag = awaitTouchSlopOrCancellation(down.id) { change, over ->
                                change.consumePositionChange()
                                overSlop = over
                            }
                        } while (drag != null && !drag.positionChangeConsumed())
                        if (drag != null) {
                            dragOffset = Offset.Zero
                            if (scale !in 0.92f..1.08f) {
                                offset += overSlop
                            } else {
                                dragOffset += overSlop
                            }
                            if (drag(drag.id) {
                                    if (scale !in 0.92f..1.08f) {
                                        offset += it.positionChange()
                                    } else {
                                        dragOffset += it.positionChange()
                                    }
                                    it.consumePositionChange()
                                }
                            ) {
                                if (scale in 0.92f..1.08f) {
                                    val offsetX = dragOffset.x
                                    if (offsetX > 300) {
                                        onSwipeRight()

                                    } else if (offsetX < -300) {
                                        onSwipeLeft()
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        ZoomComposable(
            modifier = Modifier
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                .graphicsLayer(
                    scaleX = scale - 0.02f,
                    scaleY = scale - 0.02f,
                    rotationZ = rotation
                )
                .onGloballyPositioned { coordinates ->
                    val localOffset =
                        Offset(
                            coordinates.size.width.toFloat() / 2,
                            coordinates.size.height.toFloat() / 2
                        )
                    val windowOffset = coordinates.localToWindow(localOffset)
                    imageCenter = coordinates.parentLayoutCoordinates?.windowToLocal(windowOffset)
                        ?: Offset.Zero
                }
        )
    }*/
}

/*
fun onTransformGesture(
    centroid: Offset,
    pan: Offset,
    zoom: Float,
    transformRotation: Float
) {
    var offset += pan
    var scale *= zoom
    var rotation += transformRotation

    val x0 = centroid.x - imageCenter.x
    val y0 = centroid.y - imageCenter.y

    val hyp0 = sqrt(x0 * x0 + y0 * y0)
    val hyp1 = zoom * hyp0 * (if (x0 > 0) {
        1f
    } else {
        -1f
    })

    val alpha0 = atan(y0 / x0)

    val alpha1 = alpha0 + (transformRotation * ((2 * PI) / 360))

    val x1 = cos(alpha1) * hyp1
    val y1 = sin(alpha1) * hyp1

    transformOffset =
        centroid - (imageCenter - offset) - Offset(x1.toFloat(), y1.toFloat())
    offset = transformOffset
}*/
