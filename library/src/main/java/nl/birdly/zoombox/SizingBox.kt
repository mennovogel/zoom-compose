package nl.birdly.zoombox

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.IntSize

/**
 * // TODO: Work in progress
 *
 * This doesn't work properly yet:
 * Issues: Alignment, doesn't behave the same as Box.
 */
@Composable
fun SizingBox(
    modifier: Modifier = Modifier,
    onBoxSizeChanged: (IntSize) -> Unit,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        Layout(
            content = content
        ) { measurables, constraints ->
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }

            // TODO: Why do we need to calculate the totalHeight, instead of constraints.maxHeight?
            val totalHeight = placeables
                .map { it.height }
                .reduce { acc, height -> acc + height }

            val boxSize = IntSize(constraints.maxWidth, constraints.maxHeight)
            onBoxSizeChanged(boxSize)

            layout(boxSize.width, totalHeight) {
                placeables.forEach { placeable ->
                    placeable.placeRelative(0, 0)
                }
            }
        }
    }
}

@Composable
fun SizingBox(
    modifier: Modifier = Modifier,
    onBoxSizeChanged: (IntSize) -> Unit,
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        // TODO: Why do we need to calculate the totalHeight, instead of constraints.maxHeight?
        val totalHeight = placeables
            .map { it.height }
            .reduce { acc, height -> acc + height }

        val boxSize = IntSize(constraints.maxWidth, constraints.maxHeight)
        onBoxSizeChanged(boxSize)

        Log.d("Menno", "SizingBox: totalHeight=$totalHeight, boxSize=$boxSize")

        layout(boxSize.width, totalHeight) {
            placeables.forEach { placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}