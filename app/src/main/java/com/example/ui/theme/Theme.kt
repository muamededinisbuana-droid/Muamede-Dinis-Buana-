package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BuanaTealPrimary,
    onPrimary = BuanaTealOnPrimary,
    primaryContainer = BuanaTealContainer,
    onPrimaryContainer = BuanaTealOnContainer,
    secondary = BuanaAmberSecondary,
    onSecondary = BuanaAmberOnSecondary,
    secondaryContainer = BuanaAmberContainer,
    onSecondaryContainer = BuanaAmberOnContainer,
    tertiary = BuanaTerracottaTertiary,
    onTertiary = BuanaTerracottaOnTertiary,
    tertiaryContainer = BuanaTerracottaContainer,
    onTertiaryContainer = BuanaTerracottaOnContainer,
    background = BuanaBackgroundLight,
    onBackground = BuanaOnSurfaceLight,
    surface = BuanaSurfaceLight,
    onSurface = BuanaOnSurfaceLight,
    surfaceVariant = BuanaSurfaceVariantLight,
    onSurfaceVariant = BuanaOnSurfaceVariantLight,
    outline = BuanaOutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BuanaTealPrimaryDark,
    onPrimary = BuanaTealOnPrimaryDark,
    primaryContainer = BuanaTealContainerDark,
    onPrimaryContainer = BuanaTealOnContainerDark,
    secondary = BuanaAmberSecondaryDark,
    onSecondary = BuanaAmberOnSecondaryDark,
    secondaryContainer = BuanaAmberContainerDark,
    onSecondaryContainer = BuanaAmberOnContainerDark,
    background = BuanaBackgroundDark,
    onBackground = BuanaOnSurfaceDark,
    surface = BuanaSurfaceDark,
    onSurface = BuanaOnSurfaceDark,
    surfaceVariant = BuanaSurfaceVariantDark,
    onSurfaceVariant = BuanaOnSurfaceVariantDark
)

@Composable
fun BuanaAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded colors by default for consistent accessible experience
    content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
  )
}
