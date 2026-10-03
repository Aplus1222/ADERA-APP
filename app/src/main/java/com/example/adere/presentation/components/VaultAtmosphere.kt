package com.example.adere.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalThemeAccents

/**
 * Renders an atmospheric security background with a rich radial gradient glow at top,
 * subtle cryptographic circuit gridlines, and layered depth.
 */
@Suppress("unused")
@Composable
fun VaultAtmosphereBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val accents = LocalThemeAccents.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(accents.background)
    ) {
        // Ambient subtle cyber grid & radial top glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top-centered soft ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accents.primaryGlow.copy(alpha = 0.22f),
                        accents.primaryGlow.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, 0f),
                    radius = width * 0.85f
                ),
                radius = width * 0.85f,
                center = Offset(width * 0.5f, 0f)
            )

            // Subtle cyber-security dot/mesh grid
            val gridStep = 44f
            val dotColor = accents.border.copy(alpha = 0.12f)
            var x = 0f
            while (x < width) {
                var y = 0f
                while (y < height) {
                    drawCircle(
                        color = dotColor,
                        radius = 1.2f,
                        center = Offset(x, y)
                    )
                    y += gridStep
                }
                x += gridStep
            }
        }

        content()
    }
}

/**
 * Premium Vault Card with styled gradient outline, soft dark elevation, and rounded corners.
 */
@Suppress("unused")
@Composable
fun VaultGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    colors: CardColors? = null,
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    val accents = LocalThemeAccents.current
    val effectiveColors = colors ?: CardDefaults.cardColors(
        containerColor = accents.surface
    )

    Card(
        modifier = modifier
            .clip(shape)
            .border(
                width = borderWidth,
                brush = accents.cardBorderGradient,
                shape = shape
            ),
        shape = shape,
        colors = effectiveColors
    ) {
        content()
    }
}
