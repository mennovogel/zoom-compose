package nl.birdly.zoom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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