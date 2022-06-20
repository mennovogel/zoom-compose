package nl.birdly.zoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoombox.Zoomable
import nl.birdly.zoombox.gesture.transform.KeepWithinBoundsOnCancelledBehavior
import nl.birdly.zoombox.gesture.transform.TransformGestureHandler

@Composable
fun ImageScreen(asset: String) {
    val bitmap: Bitmap = LocalContext.current.assetsToBitmap(asset)
    Zoomable(
        Modifier
            .fillMaxHeight(),
        transformGestureHandler = TransformGestureHandler(
            onCancelledBehavior = KeepWithinBoundsOnCancelledBehavior(),
        ),
        zoomRange = 1f..4f
    ) {
        Image(
            bitmap.asImageBitmap(),
            contentDescription = asset,
        )
    }
}

private fun Context.assetsToBitmap(fileName: String): Bitmap {
    return with(assets.open(fileName)){
        BitmapFactory.decodeStream(this)
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    ZoomTheme {
        ImageScreen("Dolphin.jpg")
    }
}