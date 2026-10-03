@file:Suppress("unused")

package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
    val cardBorderGradient: Brush,
)

val LocalThemeAccents = staticCompositionLocalOf {
    ThemeAccents(
        primary = TotalSecurityPrimary,
        primaryLight = TotalSecurityPrimaryLight,
        primaryGlow = EmeraldGlow,
        surface = CleanSurface,
        surfaceElevated = CleanSurface,
        background = CleanBg,
        border = CleanBorder,
        borderGlow = TotalSecurityPrimary,
        ambientGradient = Brush.verticalGradient(listOf(Color(0xFFEEF0FF), CleanBg)),
        cardBorderGradient = Brush.linearGradient(listOf(CleanBorder, CleanBorderSubtle))
    )
}

private fun getAppColorScheme(palette: VaultThemePalette): ColorScheme {
    return when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> lightColorScheme(
            primary = TotalSecurityPrimary,
            onPrimary = Color.White,
            primaryContainer = TotalSecurityPrimaryContainer,
            onPrimaryContainer = TotalSecurityOnPrimaryContainer,
            secondary = CategoryAppsYellow,
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFEF3C7),
            onSecondaryContainer = Color(0xFF78350F),
            tertiary = CategoryCardTeal,
            onTertiary = Color.White,
            background = CleanBg,
            onBackground = TextDarkPrimary,
            surface = CleanSurface,
            onSurface = TextDarkPrimary,
            surfaceVariant = CleanSurfaceVariant,
            onSurfaceVariant = TextDarkSecondary,
            outline = CleanBorder,
            outlineVariant = CleanBorderSubtle,
            error = HealthRiskRed,
            onError = Color.White
        )
        VaultThemePalette.OBSIDIAN_EMERALD -> darkColorScheme(
            primary = Color(0xFF10B981),
            onPrimary = Color(0xFF003824),
            primaryContainer = Color(0xFF064E3B),
            onPrimaryContainer = Color(0xFFA7F3D0),
            secondary = GoldAccent,
            onSecondary = Color(0xFF452200),
            secondaryContainer = Color(0xFF78350F),
            onSecondaryContainer = Color(0xFFFDE68A),
            tertiary = Color(0xFF38BDF8),
            onTertiary = Color(0xFF003258),
            background = Color(0xFF070B0A),
            onBackground = Color(0xFFF8FAFC),
            surface = Color(0xFF0F1715),
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = Color(0xFF16221F),
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = Color(0xFF203630),
            outlineVariant = Color(0xFF142420),
            error = HealthRiskRed,
            onError = Color.White
        )
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> darkColorScheme(
            primary = SapphirePrimary,
            onPrimary = Color(0xFF003258),
            primaryContainer = SapphireContainer,
            onPrimaryContainer = Color(0xFFC2E7FF),
            secondary = Color(0xFF34D399),
            onSecondary = Color(0xFF003824),
            secondaryContainer = Color(0xFF064E3B),
            onSecondaryContainer = Color(0xFFA7F3D0),
            tertiary = Color(0xFFA855F7),
            onTertiary = Color(0xFF38006B),
            background = SapphireBg,
            onBackground = Color(0xFFF8FAFC),
            surface = SapphireSurface,
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = SapphireSurfaceVariant,
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = SapphireBorder,
            outlineVariant = Color(0xFF101B2E),
            error = HealthRiskRed,
            onError = Color.White
        )
    }
}

private fun getThemeAccents(palette: VaultThemePalette): ThemeAccents {
    return when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> ThemeAccents(
            primary = TotalSecurityPrimary,
            primaryLight = TotalSecurityPrimaryLight,
            primaryGlow = Color(0x265E5CE6),
            surface = CleanSurface,
            surfaceElevated = CleanSurface,
            background = CleanBg,
            border = CleanBorder,
            borderGlow = TotalSecurityPrimary,
            ambientGradient = Brush.verticalGradient(listOf(Color(0xFFF1F3FF), CleanBg)),
            cardBorderGradient = Brush.linearGradient(listOf(CleanBorder, CleanBorderSubtle))
        )
        VaultThemePalette.OBSIDIAN_EMERALD -> ThemeAccents(
            primary = Color(0xFF10B981),
            primaryLight = Color(0xFF34D399),
            primaryGlow = Color(0x3310B981),
            surface = Color(0xFF0F1715),
            surfaceElevated = Color(0xFF1B2C27),
            background = Color(0xFF070B0A),
            border = Color(0xFF203630),
            borderGlow = Color(0xFF2D5047),
            ambientGradient = Brush.verticalGradient(listOf(Color(0xFF0F2620), Color(0xFF070B0A))),
            cardBorderGradient = Brush.linearGradient(listOf(Color(0xFF2D5047), Color(0xFF203630)))
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
    }
}

@Composable
fun AdereTheme(
    palette: VaultThemePalette = VaultThemePalette.TOTAL_SECURITY,
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = palette != VaultThemePalette.TOTAL_SECURITY,
    content: @Composable () -> Unit
) {
    val colorScheme = getAppColorScheme(palette)
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
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    AdereTheme(darkTheme = darkTheme, content = content)
}
