package com.example.medirecord4.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Primary,
    background = Background,
    surface = Surface,
    onPrimary = Surface,
    onSurface = TextPrimary,
    onBackground = TextPrimary,
)

private val DarkColors = darkColorScheme(
    primary = Primary,
    secondary = Primary,
    background = TextPrimary,
    surface = TextPrimary,
    onPrimary = Surface,
    onSurface = Background,
    onBackground = Background,
)

@Composable
fun MediRecordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = TypographyIOS,
        shapes = ShapesIOS,
        content = content
    )
}
