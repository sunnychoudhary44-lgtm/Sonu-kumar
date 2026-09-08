package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldAccent,
    onPrimary = Slate900,
    primaryContainer = NavyLight,
    onPrimaryContainer = GoldLight,
    secondary = TealAccent,
    onSecondary = Color.White,
    background = Slate900,
    surface = Slate800,
    onBackground = Slate50,
    onSurface = Slate50,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate100
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Slate100,
    onPrimaryContainer = NavyPrimary,
    secondary = GoldAccent,
    onSecondary = Slate900,
    secondaryContainer = GoldLight,
    onSecondaryContainer = Slate900,
    tertiary = TealAccent,
    background = Slate50,
    surface = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = Slate100,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFCBD5E1)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

