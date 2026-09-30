package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class ThemeAccents(
    val primary: Color,
    val primaryLight: Color,
    val primaryGlow: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val background: Color,
    val border: Color,
    val borderGlow: Color,
    val ambientGradient: Brush,
    val cardBorderGradient: Brush
)

val LocalThemeAccents = staticCompositionLocalOf {
    ThemeAccents(
        primary = EmeraldPrimary,
        primaryLight = EmeraldLight,
        primaryGlow = EmeraldGlow,
        surface = CharcoalSurface,
        surfaceElevated = CharcoalSurfaceElevated,
        background = CharcoalBg,
        border = CharcoalBorder,
        borderGlow = CharcoalBorderGlow,
        ambientGradient = Brush.verticalGradient(listOf(Color(0xFF0F2620), CharcoalBg)),
        cardBorderGradient = Brush.linearGradient(listOf(CharcoalBorderGlow, CharcoalBorder))
    )
}

private fun getDarkColorScheme(palette: VaultThemePalette): ColorScheme {
    return when (palette) {
        VaultThemePalette.OBSIDIAN_EMERALD -> darkColorScheme(
            primary = EmeraldPrimary,
            onPrimary = Color(0xFF003824),
            primaryContainer = EmeraldContainer,
            onPrimaryContainer = OnEmeraldContainer,
            secondary = GoldAccent,
            onSecondary = Color(0xFF452200),
            secondaryContainer = GoldContainer,
            onSecondaryContainer = OnGoldContainer,
            tertiary = SecurityBlue,
            onTertiary = Color(0xFF003258),
            background = CharcoalBg,
            onBackground = TextPrimary,
            surface = CharcoalSurface,
            onSurface = TextPrimary,
            surfaceVariant = CharcoalSurfaceVariant,
            onSurfaceVariant = TextSecondary,
            outline = CharcoalBorder,
            outlineVariant = CharcoalBorderSubtle,
            error = SecurityRed,
            onError = Color.White
        )
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> darkColorScheme(
            primary = SapphirePrimary,
            onPrimary = Color(0xFF003258),
            primaryContainer = SapphireContainer,
            onPrimaryContainer = Color(0xFFC2E7FF),
            secondary = EmeraldLight,
            onSecondary = Color(0xFF003824),
            secondaryContainer = EmeraldContainer,
            onSecondaryContainer = OnEmeraldContainer,
            tertiary = SecurityPurple,
            onTertiary = Color(0xFF38006B),
            background = SapphireBg,
            onBackground = TextPrimary,
            surface = SapphireSurface,
            onSurface = TextPrimary,
            surfaceVariant = SapphireSurfaceVariant,
            onSurfaceVariant = TextSecondary,
            outline = SapphireBorder,
            outlineVariant = Color(0xFF101B2E),
            error = SecurityRed,
            onError = Color.White
        )
        VaultThemePalette.AMETHYST_CRYPT -> darkColorScheme(
            primary = AmethystPrimary,
            onPrimary = Color(0xFF38006B),
            primaryContainer = AmethystContainer,
            onPrimaryContainer = Color(0xFFF3E8FF),
            secondary = EmeraldLight,
            onSecondary = Color(0xFF003824),
            secondaryContainer = EmeraldContainer,
            onSecondaryContainer = OnEmeraldContainer,
            tertiary = GoldAccent,
            onTertiary = Color(0xFF452200),
            background = AmethystBg,
            onBackground = TextPrimary,
            surface = AmethystSurface,
            onSurface = TextPrimary,
            surfaceVariant = AmethystSurfaceVariant,
            onSurfaceVariant = TextSecondary,
            outline = AmethystBorder,
            outlineVariant = Color(0xFF1E1436),
            error = SecurityRed,
            onError = Color.White
        )
    }
}

private fun getThemeAccents(palette: VaultThemePalette): ThemeAccents {
    return when (palette) {
        VaultThemePalette.OBSIDIAN_EMERALD -> ThemeAccents(
            primary = EmeraldPrimary,
            primaryLight = EmeraldLight,
            primaryGlow = EmeraldGlow,
            surface = CharcoalSurface,
            surfaceElevated = CharcoalSurfaceElevated,
            background = CharcoalBg,
            border = CharcoalBorder,
            borderGlow = CharcoalBorderGlow,
            ambientGradient = Brush.verticalGradient(listOf(Color(0xFF0F2620), CharcoalBg)),
            cardBorderGradient = Brush.linearGradient(listOf(CharcoalBorderGlow, CharcoalBorder))
        )
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> ThemeAccents(
            primary = SapphirePrimary,
            primaryLight = SapphireLight,
            primaryGlow = SapphireGlow,
            surface = SapphireSurface,
            surfaceElevated = Color(0xFF142033),
            background = SapphireBg,
            border = SapphireBorder,
            borderGlow = Color(0xFF254670),
            ambientGradient = Brush.verticalGradient(listOf(Color(0xFF0C213D), SapphireBg)),
            cardBorderGradient = Brush.linearGradient(listOf(Color(0xFF254670), SapphireBorder))
        )
        VaultThemePalette.AMETHYST_CRYPT -> ThemeAccents(
            primary = AmethystPrimary,
            primaryLight = AmethystLight,
            primaryGlow = AmethystGlow,
            surface = AmethystSurface,
            surfaceElevated = Color(0xFF22173D),
            background = AmethystBg,
            border = AmethystBorder,
            borderGlow = Color(0xFF48307A),
            ambientGradient = Brush.verticalGradient(listOf(Color(0xFF241042), AmethystBg)),
            cardBorderGradient = Brush.linearGradient(listOf(Color(0xFF48307A), AmethystBorder))
        )
    }
}

@Composable
fun AdereTheme(
    palette: VaultThemePalette = VaultThemePalette.OBSIDIAN_EMERALD,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = getDarkColorScheme(palette)
    val accents = getThemeAccents(palette)

    CompositionLocalProvider(LocalThemeAccents provides accents) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    AdereTheme(darkTheme = darkTheme, content = content)
}
