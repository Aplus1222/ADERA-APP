package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Total Security (Image Theme) Palette
// ==========================================
val TotalSecurityPrimary = Color(0xFF5E5CE6) // Royal Indigo / Periwinkle
val TotalSecurityPrimaryLight = Color(0xFF7E7CF6)
val TotalSecurityPrimaryDark = Color(0xFF4341B8)
val TotalSecurityPrimaryContainer = Color(0xFFEEF0FF) // Lavender password card container
val TotalSecurityOnPrimaryContainer = Color(0xFF27237E)

// Category Colors from Image
val CategorySocialBlue = Color(0xFF5E5CE6) // Social tile
val CategoryAppsYellow = Color(0xFFF5BA31) // Apps tile
val CategoryCardTeal = Color(0xFF48C9B0)   // Card tile
val CategoryCoral = Color(0xFFFF6B6B)

// Action & Utility Colors from Image
val ActionCopyGreen = Color(0xFF6EE7B7)    // "Copy to clipboard" button
val ActionCopyGreenText = Color(0xFF065F46)
val ActionRefreshYellow = Color(0xFFFEF08A) // Circular refresh button
val ActionRefreshYellowIcon = Color(0xFF78350F)

// Health Ring Colors from Image
val HealthSafeBlue = Color(0xFF5E5CE6)
val HealthRefusedYellow = Color(0xFFF5BA31)
val HealthRiskRed = Color(0xFFEF4444)
val HealthCompromisedTeal = Color(0xFF48C9B0)

// Surfaces & Backgrounds
val CleanBg = Color(0xFFF7F8FC)
val CleanSurface = Color(0xFFFFFFFF)
val CleanSurfaceVariant = Color(0xFFF1F3F9)
val CleanBorder = Color(0xFFECEEF5)
val CleanBorderSubtle = Color(0xFFF3F4F8)

// Typography Colors
val TextDarkPrimary = Color(0xFF1A1C29)
val TextDarkSecondary = Color(0xFF737A8C)
val TextDarkMuted = Color(0xFFA1A7B7)

// Backward Compatibility Aliases
val CharcoalBg = CleanBg
val CharcoalSurface = CleanSurface
val CharcoalSurfaceVariant = CleanSurfaceVariant
val CharcoalSurfaceElevated = CleanSurface
val CharcoalBorder = CleanBorder
val CharcoalBorderSubtle = CleanBorderSubtle
val CharcoalBorderGlow = Color(0xFFD6DAE8)

val EmeraldPrimary = TotalSecurityPrimary
val EmeraldLight = TotalSecurityPrimaryLight
val EmeraldDark = TotalSecurityPrimaryDark
val EmeraldContainer = TotalSecurityPrimaryContainer
val OnEmeraldContainer = TotalSecurityOnPrimaryContainer
val EmeraldGlow = Color(0x265E5CE6)

val GoldAccent = CategoryAppsYellow
val GoldLight = Color(0xFFFBBF24)
val GoldContainer = Color(0xFFFEF3C7)
val OnGoldContainer = Color(0xFF78350F)
val GoldGlow = Color(0x33F5BA31)

// Sapphire Palette for theme switcher
val SapphirePrimary = Color(0xFF38BDF8)
val SapphireLight = Color(0xFF7DD3FC)
val SapphireDark = Color(0xFF0369A1)
val SapphireContainer = Color(0xFF0C4A6E)
val SapphireGlow = Color(0x3338BDF8)
val SapphireBg = Color(0xFF060B12)
val SapphireSurface = Color(0xFF0D1726)
val SapphireSurfaceVariant = Color(0xFF132238)
val SapphireBorder = Color(0xFF1D3557)

val TextPrimary = TextDarkPrimary
val TextSecondary = TextDarkSecondary
val TextMuted = TextDarkMuted

val SecurityGreen = HealthCompromisedTeal
val SecurityYellow = HealthRefusedYellow
val SecurityOrange = Color(0xFFF97316)
val SecurityRed = HealthRiskRed
val SecurityBlue = TotalSecurityPrimary
val SecurityPurple = Color(0xFFA855F7)

val LightBg = CleanBg
val LightSurface = CleanSurface
val LightSurfaceVariant = CleanSurfaceVariant
val LightBorder = CleanBorder
val LightTextPrimary = TextDarkPrimary
val LightTextSecondary = TextDarkSecondary

// Theme Palette Enum
enum class VaultThemePalette(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val surfaceColor: Color,
    val bgColor: Color
) {
    TOTAL_SECURITY(
        title = "Total Security",
        subtitle = "Clean Purple & Modern (Image Theme)",
        primaryColor = Color(0xFF5E5CE6),
        surfaceColor = Color(0xFFFFFFFF),
        bgColor = Color(0xFFF7F8FC)
    ),
    OBSIDIAN_EMERALD(
        title = "Obsidian Emerald",
        subtitle = "Dark Cyber Cipher",
        primaryColor = Color(0xFF10B981),
        surfaceColor = Color(0xFF0F1715),
        bgColor = Color(0xFF070B0A)
    ),
    MIDNIGHT_SAPPHIRE(
        title = "Midnight Sapphire",
        subtitle = "Deep Defense Oceanic",
        primaryColor = Color(0xFF38BDF8),
        surfaceColor = Color(0xFF0D1726),
        bgColor = Color(0xFF060B12)
    )
}
