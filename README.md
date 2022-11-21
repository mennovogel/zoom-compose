# Zoom Compose

Zoom Compose is an Android library to zoom in Composables using pinch and double-tap gestures. It
supports both zooming inside lists as zooming on a separate screen.

![](https://github.com/mennovogel/zoom-compose/raw/main/preview.gif)

## Usage

1. Add the Zoom library to your project:

Add Jitpack to your repositories, in your top-level `build.gradle` or `settings.gradle` file:

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```
Then add the library to your dependencies in your app-level `build.gradle` file:
```gradle
dependencies {
    implementation 'com.github.mennovogel:zoom-compose:${zoom-compose.version}'
}
```

2. Usage in your project:

   Basic usage: 

```kotlin
Image(
   bitmap,
   contentDescription,
   Modifier.zoomable()
)
```

This will make your image, or any composable zoomable with all the defaults. You can now pinch 
to zoom and double tap to zoom. The image will be remember it's position when zooming is stopped.
The image will be centered automatically.   

   Additional options:

```kotlin
// This enables you to observe changes, like zoomState.value.scale. 
val zoomState = rememberMutableZoomState()

Card(
   Modifier
      .padding(8.dp)
      .zoomable(
         zoomState = zoomState,
         // Provide your preferred zoomRange.
         zoomRange = 0.8f..3f,
         // Add tap or double tap handlers.
         tapHandler = TapHandler(
            onDoubleTap = null,
            onTap = {
               onTap(asset)
            }
         ),
         // Use another onCancelledBehavior than the default KeepWithinBoundsOnCancelledBehavior.
         transformGestureHandler = TransformGestureHandler(
            // ResetToOriginalPositionOnCancelledBehavior always resets to the original position after 
            // the composable has been touched.
            onCancelledBehavior = ResetToOriginalPositionOnCancelledBehavior(),
            // OnDoubleTouchCondition makes sure the composable is only transformed with double touch 
            // gestures. This makes sense when used inside a list. 
            onCondition = OnDoubleTouchCondition()
         )
      )
   ,
   // You can easily use the scale for example.
   elevation = (zoomState.value.scale * 10f).dp
) {
   Image(bitmap, contentDescription)
}
```

Using a HorizontalPager? Then use WithinXBoundsTouchCondition to combine paging and zooming
```kotlin
val pagerState = rememberPagerState()

VerticalPager(viewModel.images.size, state = pagerState) {
    val image = viewModel.images[it]

   Image(
      bitmap,
      image.name,
      Modifier.zoomable(
         transformGestureHandler = TransformGestureHandler(
            // This stops zooming when zoomed out of bounds to allow paging
            onCondition = WithinXBoundsTouchCondition()
         )
      )
   )
}
```

## Licence

Zoom Compose is available under the MIT license.