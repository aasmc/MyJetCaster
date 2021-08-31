package ru.aasmc.myjetcaster.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min


fun Color.contrastAgainst(background: Color): Float {
    // Composites this color on top of background using the Porter-Duff 'source over' mode.
    val fg = if (alpha < 1f) compositeOver(background) else this

    // luminance is a linear measure of light,
    // read more here: https://developer.mozilla.org/en-US/docs/Web/Accessibility/Understanding_Colors_and_Luminance
    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}