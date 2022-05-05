package nl.birdly.zoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import nl.birdly.zoombox.gesture.tap.TapHandler
import nl.birdly.zoombox.gesture.transform.OnDoubleTouchCondition
import nl.birdly.zoombox.gesture.transform.TransformGestureHandler
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoombox.Zoomable
import nl.birdly.zoombox.gesture.transform.ResetOnCanceledHandler

@Composable
fun MainScreen(navController: NavHostController) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        val onTap = { asset: String ->
            navController.navigate("image/$asset")
        }

        ImageRow(title = "Dolphin", asset = "Dolphin.jpg", onTap)
        ImageRow(title = "Mountains", asset = "Mountains.jpg", onTap)
        ImageRow(title = "Shanghai", asset = "Shanghai.jpg", onTap)
        ImageRow(title = "Sunset", asset = "Sunset.jpg", onTap)
    }
}

@Composable
fun ImageRow(title: String, asset: String, onTap: (String) -> Unit) {
    Text(text = title, style = MaterialTheme.typography.h6, modifier = Modifier.padding(
        start = 16.dp, top = 24.dp, bottom = 8.dp
    ))
    val bitmap: Bitmap = LocalContext.current.assetsToBitmap(asset)
    Zoomable(
        tapHandler = TapHandler(
            onDoubleTap = null,
            onTap = {
                onTap(asset)
            }
        ),
        transformGestureHandler = TransformGestureHandler(
            onCanceled = ResetOnCanceledHandler(),
            onCondition = OnDoubleTouchCondition()
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
        MainScreen(rememberNavController())
    }
}