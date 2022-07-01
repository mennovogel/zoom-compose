package nl.birdly.zoombox.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// TODO: Is this what I want?
fun Modifier.swipeToDismiss(
    onDismissed: () -> Unit
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    pointerInput(Unit) {
        // Used to calculate fling decay.
        val decay = splineBasedDecay<Float>(this)
        // Use suspend functions for touch events and the Animatable.
        coroutineScope {
            while (true) {
                // Detect a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                val velocityTracker = VelocityTracker()
                // Stop any ongoing animation.
                offsetX.stop()
                offsetY.stop()
                awaitPointerEventScope {
                    drag(pointerId) { change ->
                        launch {
                            offsetX.snapTo(
                                offsetX.value + change.positionChange().x
                            )
                            offsetY.snapTo(
                                offsetY.value + change.positionChange().y
                            )
                        }
                        velocityTracker.addPosition(
                            change.uptimeMillis,
                            change.position
                        )
                    }
                }
                // No longer receiving touch events. Prepare the animation.
                val velocityX = velocityTracker.calculateVelocity().x
                val targetOffsetX = decay.calculateTargetValue(
                    offsetX.value,
                    velocityX
                )
                val velocityY = velocityTracker.calculateVelocity().x
                val targetOffsetY = decay.calculateTargetValue(
                    offsetY.value,
                    velocityY
                )
                // The animation stops when it reaches the bounds.
                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                offsetY.updateBounds(
                    lowerBound = -size.height.toFloat(),
                    upperBound = size.height.toFloat()
                )
                launch {
                    if (targetOffsetX.absoluteValue <= size.width &&
                            targetOffsetY.absoluteValue <= size.height) {
                        // Not enough velocity; Slide back.
                        offsetX.animateTo(0f, initialVelocity = velocityX)
                        offsetY.animateTo(0f, initialVelocity = velocityY)
                    } else {
                        // The element was swiped away.
                        offsetX.animateDecay(velocityX, decay)
                        offsetY.animateDecay(velocityY, decay)
                        onDismissed()
                    }
                }
            }
        }
    }
        .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
}