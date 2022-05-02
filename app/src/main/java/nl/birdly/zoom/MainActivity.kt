package nl.birdly.zoom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nl.birdly.zoom.ui.theme.ZoomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZoomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") { MainScreen(navController) }
                        composable("image/{asset}") { navBackStackEntry ->
                            ImageScreen(navBackStackEntry.arguments?.getString("asset")!!)
                        }
                    }
                }
            }
        }
    }
}