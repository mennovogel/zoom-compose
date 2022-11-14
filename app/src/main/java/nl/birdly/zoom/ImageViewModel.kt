package nl.birdly.zoom

import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {

    val images = listOf(
        Image("Highlander", "Highlander.jpg"),
        Image("Lizard", "Lizard.jpg"),
        Image("Dolphin", "Dolphin.jpg"),
        Image("Shanghai", "Shanghai.jpg"),
    )
}