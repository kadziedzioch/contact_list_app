package com.example.contactlistapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    primary = light_green,
    primaryVariant = cream,
    secondary = Color.White,
    background = darker_green,
    surface = very_light_green,
    onPrimary = Color.White,
    onSecondary = darker_green,
    onBackground = cream,
    onSurface = Color.Black
)

private val LightColorPalette = lightColors(
    primary = light_green,
    primaryVariant = darker_green,
    secondary = very_light_green,
    background = cream,
    surface = Color.White,
    onPrimary = cream,
    onSecondary = darker_green,
    onBackground = darker_green,
    onSurface = darker_green
)

@Composable
fun ContactListAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            window.navigationBarColor = colors.primary.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = darkTheme
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = darkTheme
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}