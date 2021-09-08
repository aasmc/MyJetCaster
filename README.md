# Learning Project MyJetcaster 

Based on official Google Jetpack Compose sample Jetcaster https://github.com/android/compose-samples/tree/main/Jetcaster.

I rewrote Jetcaster from scratch to recollect some advanced techniques relating to Jetpack Compose. 

Dynamic theming

The home screen currently implements dynamic theming, using the artwork of the currently selected podcast from the carousel to update the primary and onPrimary colors. 

This is implemented in DynamicTheming.kt, which provides the DynamicThemePrimaryColorsFromImage composable, to automatically animate the theme colors based on the provided image URL, like so:

```kotlin
val dominantColorState: DominantColorState = rememberDominantColorState()

DynamicThemePrimaryColorsFromImage(dominantColorState) {
    var imageUrl = remember { mutableStateOf("") }

    // When the image url changes, call updateColorsFromImageUrl()
    launchInComposition(imageUrl) {
        dominantColorState.updateColorsFromImageUrl(imageUrl)
    }

    // Content which will be dynamically themed....
}
```

Underneath, DominantColorState uses the Coil library to fetch the artwork image 🖼️, and then Palette to extract the dominant colors from the image

## Architecture

The app is built in a Redux-style, where each UI 'screen' has its own ViewModel, which exposes a single StateFlow containing the entire view state. Each ViewModel is responsible for subscribing to any data streams required for the view, as well as exposing functions which allow the UI to send events.

