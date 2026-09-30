package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// 1. OBSIDIAN EMERALD PALETTE (Primary Vault)
// ==========================================
val EmeraldPrimary = Color(0xFF10B981)
val EmeraldLight = Color(0xFF34D399)
val EmeraldDark = Color(0xFF065F46)
val EmeraldContainer = Color(0xFF064E3B)
val OnEmeraldContainer = Color(0xFFA7F3D0)
val EmeraldGlow = Color(0x3310B981)

val GoldAccent = Color(0xFFF59E0B)
val GoldLight = Color(0xFFFBBF24)
val GoldContainer = Color(0xFF78350F)
val OnGoldContainer = Color(0xFFFDE68A)
val GoldGlow = Color(0x33F59E0B)

// Obsidian Surfaces (Extra Rich, Deep Layered Tones)
val CharcoalBg = Color(0xFF070B0A)
val CharcoalSurface = Color(0xFF0F1715)
val CharcoalSurfaceVariant = Color(0xFF16221F)
val CharcoalSurfaceElevated = Color(0xFF1B2C27)
val CharcoalBorder = Color(0xFF203630)
val CharcoalBorderSubtle = Color(0xFF142420)
val CharcoalBorderGlow = Color(0xFF2D5047)

// ==========================================
// 2. MIDNIGHT SAPPHIRE PALETTE (Deep Defense)
// ==========================================
val SapphirePrimary = Color(0xFF38BDF8)
val SapphireLight = Color(0xFF7DD3FC)
val SapphireDark = Color(0xFF0369A1)
val SapphireContainer = Color(0xFF0C4A6E)
val SapphireGlow = Color(0x3338BDF8)

val SapphireBg = Color(0xFF060B12)
val SapphireSurface = Color(0xFF0D1726)
val SapphireSurfaceVariant = Color(0xFF132238)
val SapphireBorder = Color(0xFF1D3557)

// ==========================================
// 3. AMETHYST CRYPT PALETTE (Royal Cyber Purple)
// ==========================================
val AmethystPrimary = Color(0xFFA855F7)
val AmethystLight = Color(0xFFC084FC)
val AmethystDark = Color(0xFF6B21A8)
val AmethystContainer = Color(0xFF3B0764)
val AmethystGlow = Color(0x33A855F7)

val AmethystBg = Color(0xFF0A0712)
val AmethystSurface = Color(0xFF150F26)
val AmethystSurfaceVariant = Color(0xFF201638)
val AmethystBorder = Color(0xFF322357)

// ==========================================
// Typography Colors
// ==========================================
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// ==========================================
// Status / Health Telemetry Colors
// ==========================================
val SecurityGreen = Color(0xFF10B981)
val SecurityYellow = Color(0xFFFBBF24)
val SecurityOrange = Color(0xFFF97316)
val SecurityRed = Color(0xFFEF4444)
val SecurityBlue = Color(0xFF38BDF8)
val SecurityPurple = Color(0xFFA855F7)

// Light Theme Fallbacks
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)

// Theme Palette Enum
enum class VaultThemePalette(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val surfaceColor: Color,
    val bgColor: Color
) {
    OBSIDIAN_EMERALD(
        title = "Obsidian Emerald",
        subtitle = "Cyber Cipher (Default)",
        primaryColor = Color(0xFF10B981),
        surfaceColor = Color(0xFF0F1715),
        bgColor = Color(0xFF070B0A)
    ),
    MIDNIGHT_SAPPHIRE(
        title = "Midnight Sapphire",
        subtitle = "Defense Oceanic",
        primaryColor = Color(0xFF38BDF8),
        surfaceColor = Color(0xFF0D1726),
        bgColor = Color(0xFF060B12)
    ),
    AMETHYST_CRYPT(
        title = "Amethyst Crypt",
        subtitle = "Electric Royal",
        primaryColor = Color(0xFFA855F7),
        surfaceColor = Color(0xFF150F26),
        bgColor = Color(0xFF0A0712)
    )
}
