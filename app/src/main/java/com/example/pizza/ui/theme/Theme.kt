package com.example.pizza.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// The app always uses its light pizza palette, also when the phone is in dark mode.
private val PizzaColorScheme = lightColorScheme(
    primary = Tomato,
    onPrimary = Color.White,
    primaryContainer = TomatoSoft,
    onPrimaryContainer = TomatoDeep,
    secondary = GoldenWheat,
    onSecondary = Ink,
    secondaryContainer = WheatSoft,
    onSecondaryContainer = Ink,
    tertiary = WheatDeep,
    onTertiary = Color.White,
    tertiaryContainer = WheatSoft,
    onTertiaryContainer = Ink,
    background = Mozzarella,
    onBackground = Ink,
    surface = Mozzarella,
    onSurface = Ink,
    surfaceVariant = WheatSoft,
    onSurfaceVariant = InkMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = MozzarellaLow,
    surfaceContainer = MozzarellaContainer,
    surfaceContainerHigh = MozzarellaHigh,
    surfaceContainerHighest = MozzarellaHighest,
    outline = CrustOutline,
    outlineVariant = CrustLine
)

private val PizzaShapes = Shapes(extraLarge = RoundedCornerShape(20.dp))

@Composable
fun PizzaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PizzaColorScheme,
        typography = Typography,
        shapes = PizzaShapes,
        content = content
    )
}
