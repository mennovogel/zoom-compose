package nl.birdly.zoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoombox.Zoomable
import nl.birdly.zoombox.gesture.tap.TapHandler
import nl.birdly.zoombox.gesture.tap.ZoomOnDoubleTapHandler
import nl.birdly.zoombox.gesture.transform.KeepWithinBoundsOnCancelledBehavior
import nl.birdly.zoombox.gesture.transform.TransformGestureHandler

@Composable
fun ImageScreen(asset: String) {
    ZoomTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            val bitmap: Bitmap = LocalContext.current.assetsToBitmap(asset)
            Zoomable(
                Modifier
                    .fillMaxHeight(),
                zoomRange = 1f..4f,
                tapHandler = TapHandler(
                    ZoomOnDoubleTapHandler()
                ),
                transformGestureHandler = TransformGestureHandler(
                    onCancelledBehavior = KeepWithinBoundsOnCancelledBehavior(),
                )
            ) {
                Image(
                    bitmap.asImageBitmap(),
                    contentDescription = asset,
                )
            }
        }
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