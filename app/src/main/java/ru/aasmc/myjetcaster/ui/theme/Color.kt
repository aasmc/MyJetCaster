package ru.aasmc.myjetcaster.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

/**
 * Return the fully opaque color that results from compositing [onSurface] atop [surface] with
 * the given [alpha]. Useful for situations where semi-transparent colors are undesirable.
 */
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}

val Yellow800 = Color(0xFFF29F05)
val Red300 = Color(0xFFEA6D7E)

val JetcasterColors = darkColors(
    primary = Yellow800,
    onPrimary = Color.Black,
    primaryVariant = Yellow800,
    secondary = Yellow800,
    onSecondary = Color.Black,
    error = Red300,
    onError = Color.Black
)

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)