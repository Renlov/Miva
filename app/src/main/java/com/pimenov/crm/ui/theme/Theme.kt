package com.pimenov.crm.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Amber400,
    onPrimary = Charcoal900,
    primaryContainer = Charcoal700,
    onPrimaryContainer = Amber400,
    secondary = Warm300,
    onSecondary = Charcoal900,
    secondaryContainer = Charcoal600,
    onSecondaryContainer = Warm200,
    background = Charcoal900,
    onBackground = Warm100,
    surface = Charcoal800,
    onSurface = Warm100,
    surfaceVariant = Charcoal700,
    onSurfaceVariant = Warm300,
    error = ErrorRed,
    onError = Charcoal900,
    outline = Charcoal600
)

private val LightColorScheme = lightColorScheme(
    primary = Amber600,
    onPrimary = Color.White,
    primaryContainer = AmberLight,
    onPrimaryContainer = Amber600,
    secondary = Warm500,
    onSecondary = Color.White,
    secondaryContainer = Warm200,
    onSecondaryContainer = Charcoal800,
    background = Warm50,
    onBackground = Charcoal900,
    surface = Color.White,
    onSurface = Charcoal900,
    surfaceVariant = Warm100,
    onSurfaceVariant = Charcoal600,
    error = ErrorRedDark,
    onError = Color.White,
    outline = Warm300
)

@Composable
fun CrmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
