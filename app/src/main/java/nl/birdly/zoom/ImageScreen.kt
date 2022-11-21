package nl.birdly.zoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import nl.birdly.zoom.ui.theme.ZoomTheme
import nl.birdly.zoombox.gesture.condition.WithinXBoundsTouchCondition
import nl.birdly.zoombox.gesture.transform.TransformGestureHandler
import nl.birdly.zoombox.zoomable

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageScreen(index: Int, viewModel: ImageViewModel = ImageViewModel()) {
    ZoomTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            val pagerState = rememberPagerState()

            LaunchedEffect(index) {
                launch {
                    pagerState.scrollToPage(index)
                }
            }

            HorizontalPager(viewModel.images.size, state = pagerState) {
                val image = viewModel.images[it]

                val bitmap: ImageBitmap = LocalContext.current.assetsToBitmap(image.location)
                    .asImageBitmap()

                Image(
                    bitmap,
                    image.name,
                    Modifier.zoomable(
                        transformGestureHandler = TransformGestureHandler(
                            onCondition = WithinXBoundsTouchCondition()
                        )
                    )
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
        ImageScreen(0)
    }
}