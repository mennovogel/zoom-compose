package nl.birdly.zoom.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Secondary,
    background = Color.Black,
    onBackground = Color.White
)

private val LightColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Secondary,
    background = Color.White,
    onBackground = Color.Black
)

@Composable
fun ZoomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}