package com.example.adere.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.domain.model.VaultItem
import com.example.ui.theme.CleanBg
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.CleanSurface
import com.example.ui.theme.HealthCompromisedTeal
import com.example.ui.theme.HealthRefusedYellow
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.HealthSafeBlue
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityHealthScreen(
    healthReport: PasswordHealthAnalyzer.VaultHealthReport,
    @Suppress("UNUSED_PARAMETER") items: List<VaultItem>,
    @Suppress("UNUSED_PARAMETER") onItemClick: (String) -> Unit,
    onLockClick: () -> Unit,
    onBackClick: () -> Unit = onLockClick,
    onHealthCardClick: () -> Unit = {},
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Top Header: Back Arrow + "Password Health"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDarkPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Password Health",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary,
                            fontSize = 20.sp
                        )
                    )
                }
            }

            // Circular Multi-Color Donut Chart Hero
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.size(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        val strokeWidth = 22.dp.toPx()

                        val total = healthReport.totalPasswords.coerceAtLeast(1).toFloat()
                        val safeCount = (healthReport.totalPasswords - healthReport.weakCount - healthReport.reusedCount - healthReport.oldCount).coerceAtLeast(0)
                        val weakCount = healthReport.weakCount
                        val reusedCount = healthReport.reusedCount
                        val oldCount = healthReport.oldCount

                        if (healthReport.totalPasswords == 0) {
                            drawArc(
                                color = CleanBorder,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth)
                            )
                        } else {
                            val safeSweep = (safeCount / total) * 360f
                            val compromisedSweep = (oldCount / total) * 360f
                            val riskSweep = (weakCount / total) * 360f
                            val refusedSweep = (reusedCount / total) * 360f

                            var currentAngle = -90f

                            // 1. Safe (Royal Blue)
                            if (safeSweep > 0f) {
                                drawArc(
                                    color = HealthSafeBlue,
                                    startAngle = currentAngle,
                                    sweepAngle = safeSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                currentAngle += safeSweep
                            }
                            // 2. Compromised (Mint Teal)
                            if (compromisedSweep > 0f) {
                                drawArc(
                                    color = HealthCompromisedTeal,
                                    startAngle = currentAngle,
                                    sweepAngle = compromisedSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                currentAngle += compromisedSweep
                            }
                            // 3. Weak / Risk (Red)
                            if (riskSweep > 0f) {
                                drawArc(
                                    color = HealthRiskRed,
                                    startAngle = currentAngle,
                                    sweepAngle = riskSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                currentAngle += riskSweep
                            }
                            // 4. Refused / Reused (Yellow)
                            if (refusedSweep > 0f) {
                                drawArc(
                                    color = HealthRefusedYellow,
                                    startAngle = currentAngle,
                                    sweepAngle = refusedSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                            }
                        }
                    }

                    // Donut Center Text: Total score percentage
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = healthReport.healthScorePercent.toString(),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDarkPrimary,
                                fontSize = 32.sp
                            )
                        )
                        Text(
                            text = "Total score",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextDarkMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Legend Row matching Screen 5
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(label = "Safe", color = HealthSafeBlue)
                    LegendItem(label = "Refused", color = HealthRefusedYellow)
                    LegendItem(label = "Weak", color = HealthRiskRed)
                    LegendItem(label = "Compromised", color = HealthCompromisedTeal)
                }
            }

            // Overall Rating Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("overall_rating_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CleanBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Overall Rating",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Security & Encryption Grade",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDarkSecondary,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HealthSafeBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = healthReport.overallRating,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HealthSafeBlue,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Status Breakdown Cards (Fully Functional, non-demo):
            item {
                val safeCount = (healthReport.totalPasswords - healthReport.weakCount - healthReport.reusedCount - healthReport.oldCount).coerceAtLeast(0)
                HealthStatusCard(
                    title = "Safe Password",
                    count = safeCount,
                    icon = Icons.Default.Shield,
                    iconTint = HealthSafeBlue,
                    iconBg = Color(0xFFEEF0FF),
                    onClick = onHealthCardClick
                )
            }

            item {
                HealthStatusCard(
                    title = "Compromised Password",
                    count = healthReport.oldCount,
                    icon = Icons.Default.LockOpen,
                    iconTint = HealthCompromisedTeal,
                    iconBg = Color(0xFFE6FAF5),
                    onClick = onHealthCardClick
                )
            }

            item {
                HealthStatusCard(
                    title = "Risk Password",
                    count = healthReport.weakCount,
                    icon = Icons.Default.Warning,
                    iconTint = HealthRiskRed,
                    iconBg = Color(0xFFFEECEB),
                    onClick = onHealthCardClick
                )
            }

            item {
                HealthStatusCard(
                    title = "Refused Password",
                    count = healthReport.reusedCount,
                    icon = Icons.Default.Lock,
                    iconTint = HealthRefusedYellow,
                    iconBg = Color(0xFFFEF8E7),
                    onClick = onHealthCardClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = TextDarkPrimary,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun HealthStatusCard(
    title: String,
    count: Int,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("health_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon squircle badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = "$count found",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDarkSecondary,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}
