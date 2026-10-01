package com.example.adere.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CategoryAppsYellow
import com.example.ui.theme.TotalSecurityPrimary

/**
 * High-fidelity animated vector illustration for the onboarding hero.
 * Features fluid floating physics, pulsating security aura, shackle tilt,
 * and shimmering cyber particles.
 */
@Composable
fun TotalSecurityHeroIllustration(
    modifier: Modifier = Modifier,
    slideIndex: Int = 0
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_infinite_anim")

    // Phone gentle floating
    val phoneFloatY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phoneFloatY"
    )

    // Golden key floating in opposite phase
    val keyFloatY by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "keyFloatY"
    )

    // Padlock rotation and float
    val lockFloatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lockFloatY"
    )
    val lockTilt by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lockTilt"
    )

    // Pulsing aura radius factor
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Shimmer sparkle
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleAlpha"
    )

    Box(
        modifier = modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background circles & dynamic particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Soft pastel purple/blue background aura
            drawCircle(
                color = Color(0xFFEFF1FE),
                radius = (width * 0.42f) * pulseScale,
                center = Offset(width * 0.5f, height * 0.5f)
            )

            // Soft pastel yellow decorative aura on right
            drawCircle(
                color = Color(0xFFFEF8E7),
                radius = (width * 0.22f) * pulseScale,
                center = Offset(width * 0.76f, height * 0.45f)
            )

            // Animated cyber particles
            drawCircle(
                color = Color(0xFFF5BA31).copy(alpha = sparkleAlpha),
                radius = 5f,
                center = Offset(width * 0.2f, height * 0.25f + (keyFloatY * 0.4f))
            )
            drawCircle(
                color = Color(0xFF5E5CE6).copy(alpha = sparkleAlpha),
                radius = 6f,
                center = Offset(width * 0.82f, height * 0.2f - (phoneFloatY * 0.5f))
            )
            drawCircle(
                color = Color(0xFF10B981).copy(alpha = sparkleAlpha),
                radius = 4f,
                center = Offset(width * 0.25f, height * 0.78f + (lockFloatY * 0.3f))
            )
            drawCircle(
                color = Color(0xFFE11D48).copy(alpha = sparkleAlpha * 0.8f),
                radius = 3.5f,
                center = Offset(width * 0.85f, height * 0.72f)
            )
        }

        // Center Phone Mockup with continuous floating
        Box(
            modifier = Modifier
                .offset(y = phoneFloatY.dp)
                .size(width = 114.dp, height = 186.dp)
                .shadow(18.dp, RoundedCornerShape(22.dp), spotColor = Color(0x335E5CE6))
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Phone top speaker pill
                drawRoundRect(
                    color = Color(0xFFE2E4EC),
                    topLeft = Offset(w * 0.35f, 8f),
                    size = Size(w * 0.3f, 4f),
                    cornerRadius = CornerRadius(2f, 2f)
                )

                // Phone Avatar circle
                drawCircle(
                    color = Color(0xFFEEF0FF),
                    radius = 16f,
                    center = Offset(w * 0.5f, 42f)
                )
                drawCircle(
                    color = Color(0xFF5E5CE6),
                    radius = 8f,
                    center = Offset(w * 0.5f, 40f)
                )

                // Input field 1 (Username)
                drawRoundRect(
                    color = Color(0xFFF1F3F9),
                    topLeft = Offset(14f, 68f),
                    size = Size(w - 28f, 16f),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                // Input field 2 (Password dots)
                drawRoundRect(
                    color = Color(0xFFF1F3F9),
                    topLeft = Offset(14f, 92f),
                    size = Size(w - 28f, 16f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                // Password dot representations
                for (i in 0..4) {
                    drawCircle(
                        color = Color(0xFF5E5CE6),
                        radius = 2.5f,
                        center = Offset(24f + (i * 9f), 100f)
                    )
                }

                // Unlock button on phone screen
                drawRoundRect(
                    color = Color(0xFF5E5CE6),
                    topLeft = Offset(20f, 122f),
                    size = Size(w - 40f, 18f),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Bottom home indicator bar
                drawRoundRect(
                    color = Color(0xFFCBD5E1),
                    topLeft = Offset(w * 0.32f, h - 8f),
                    size = Size(w * 0.36f, 3f),
                    cornerRadius = CornerRadius(1.5f, 1.5f)
                )
            }
        }

        // Floating Golden Key on the Left (Animated)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = keyFloatY.dp)
                .size(50.dp)
                .shadow(10.dp, CircleShape, spotColor = CategoryAppsYellow)
                .clip(CircleShape)
                .background(Color(0xFFFEF3C7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(26.dp)
            )
        }

        // Floating Yellow Padlock on the Right (Animated with tilt)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = lockFloatY.dp)
                .graphicsLayer { rotationZ = lockTilt }
                .size(54.dp)
                .shadow(12.dp, CircleShape, spotColor = CategoryAppsYellow)
                .clip(CircleShape)
                .background(Color(0xFFFDE68A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(28.dp)
            )
        }

        // Floating Shield at Bottom Left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 10.dp, y = (-phoneFloatY * 0.8f).dp)
                .size(42.dp)
                .shadow(8.dp, CircleShape, spotColor = TotalSecurityPrimary)
                .clip(CircleShape)
                .background(Color(0xFFEEF0FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (slideIndex == 1) Icons.Default.Fingerprint else Icons.Default.Shield,
                contentDescription = null,
                tint = TotalSecurityPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
