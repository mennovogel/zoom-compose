package nl.birdly.zoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoombox.Zoomable

@Composable
fun ImageScreen(asset: String) {
    ZoomTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            val bitmap: ImageBitmap = LocalContext.current.assetsToBitmap(asset).asImageBitmap()
            Zoomable {
                Image(
                    bitmap,
                    asset,
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