package nl.birdly.zoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.birdly.zoom.gesture.tap.TapHandler
import nl.birdly.zoom.ui.theme.ZoomTheme

@Preview
@Composable
fun MainScreen() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        ImageRow(title = "Dolphin", asset = "Dolphin.jpg")
        ImageRow(title = "Mountains", asset = "Mountains.jpg")
        ImageRow(title = "Shanghai", asset = "Shanghai.jpg")
        ImageRow(title = "Sunset", asset = "Sunset.jpg")
    }
}

@Composable
fun ImageRow(title: String, asset: String) {
    Text(text = title, style = MaterialTheme.typography.h6, modifier = Modifier.padding(
        start = 16.dp, top = 24.dp, bottom = 8.dp
    ))
    val bitmap: Bitmap = LocalContext.current.assetsToBitmap(asset)
    Zoomable(
        tapHandler = TapHandler(
            onDoubleTap = null,
            onTap = {
                Log.d("Menno", "tap!")
            }
        )
    ) {
        Image(bitmap.asImageBitmap(), contentDescription = title)
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
        MainScreen()
    }
}