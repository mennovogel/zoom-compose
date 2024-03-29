package nl.birdly.zoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoombox.gesture.condition.OnDoubleTouchCondition
import nl.birdly.zoombox.gesture.tap.TapHandler
import nl.birdly.zoombox.gesture.transform.ResetToOriginalPositionOnCancelledBehavior
import nl.birdly.zoombox.gesture.transform.TransformGestureHandler
import nl.birdly.zoombox.rememberMutableZoomState
import nl.birdly.zoombox.zoomable

@Composable
fun MainScreen(navController: NavHostController, viewModel: ImageViewModel = ImageViewModel()){
    ZoomTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colors.background)
            ) {
                viewModel.images.forEachIndexed { index, image ->
                    ImageRow(contentDescription = image.name, asset = image.location) {
                        navController.navigate("image/$index")
                    }
                }
            }
        }
    }
}

@Composable
fun ImageRow(contentDescription: String, asset: String, onTap: () -> Unit) {
    Text(
        text = contentDescription,
        style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground),
        modifier = Modifier
            .padding(
                start = 16.dp, top = 24.dp, bottom = 8.dp
            )
    )
    val bitmap: ImageBitmap = LocalContext.current.assetsToBitmap(asset).asImageBitmap()

    val zoomState = rememberMutableZoomState()
    zoomState.value.scale
    Card(
        Modifier
            .padding(8.dp)
            .zoomable(
                zoomState,
                zoomRange = 0.8f..3f,
                tapHandler = TapHandler(
                    onDoubleTap = null,
                    onTap = {
                        onTap()
                    }
                ),
                transformGestureHandler = TransformGestureHandler(
                    onCancelledBehavior = ResetToOriginalPositionOnCancelledBehavior(),
                    onCondition = OnDoubleTouchCondition()
                )
            ),
        elevation = (zoomState.value.scale * 10f).dp
    ) {
        Image(bitmap, contentDescription)
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