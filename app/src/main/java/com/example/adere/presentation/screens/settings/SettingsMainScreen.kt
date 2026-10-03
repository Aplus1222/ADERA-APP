package com.example.adere.presentation.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.components.AdereTopBar
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.TotalSecurityPrimary

enum class SettingsCategory {
    SECURITY,
    PRIVACY,
    BACKUP_RESTORE,
    APPEARANCE,
    GENERATOR,
    NOTIFICATIONS,
    HELP_SUPPORT,
    ABOUT,
    DANGER_ZONE
}

@Composable
fun SettingsMainScreen(
    totalCredentialsCount: Int,
    favoritesCount: Int,
    isVaultProtected: Boolean,
    onNavigateCategory: (SettingsCategory) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val textMutedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            AdereTopBar(
                title = "Settings",
                subtitle = "Manage your vault, security, and Adera experience."
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vault Profile / Status Overview Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vault_overview_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                border = BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isVaultProtected) "Vault Protected" else "Vault Unlocked",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        fontSize = 17.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "AES-256-GCM Hardware Encrypted",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textMutedColor,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Vault Statistics Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$totalCredentialsCount Credentials",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .width(1.dp)
                                .background(borderColor)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$favoritesCount Favorites",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            // Categories Section 1: Security & Protection
            SettingsSectionTitle("Security & Vault Protection")

            SettingsItemCard(
                icon = Icons.Default.Shield,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Security",
                subtitle = "Biometrics, Auto-Lock timer, Master Password & Recovery Key",
                onClick = { onNavigateCategory(SettingsCategory.SECURITY) },
                testTag = "settings_cat_security"
            )

            SettingsItemCard(
                icon = Icons.Default.PrivacyTip,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.12f),
                title = "Privacy & Content Protection",
                subtitle = "Screenshot protection, Clipboard auto-clear & Recent Apps",
                onClick = { onNavigateCategory(SettingsCategory.PRIVACY) },
                testTag = "settings_cat_privacy"
            )

            SettingsItemCard(
                icon = Icons.Default.Save,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Backup & Restore",
                subtitle = "Encrypted vault backups, Vault restore & Emergency PDF export",
                onClick = { onNavigateCategory(SettingsCategory.BACKUP_RESTORE) },
                testTag = "settings_cat_backup"
            )

            // Categories Section 2: Customization & Utilities
            SettingsSectionTitle("Preferences & Customization")

            SettingsItemCard(
                icon = Icons.Default.ColorLens,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Appearance & Themes",
                subtitle = "Vault color palettes & immersive light/dark modes",
                onClick = { onNavigateCategory(SettingsCategory.APPEARANCE) },
                testTag = "settings_cat_appearance"
            )

            SettingsItemCard(
                icon = Icons.Default.Key,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Password Generator Defaults",
                subtitle = "Default password length, character sets & rules",
                onClick = { onNavigateCategory(SettingsCategory.GENERATOR) },
                testTag = "settings_cat_generator"
            )

            SettingsItemCard(
                icon = Icons.Default.Notifications,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.12f),
                title = "Notifications & Alerts",
                subtitle = "Vault security alerts & system permissions",
                onClick = { onNavigateCategory(SettingsCategory.NOTIFICATIONS) },
                testTag = "settings_cat_notifications"
            )

            // Categories Section 3: Information & Support
            SettingsSectionTitle("Information & Support")

            SettingsItemCard(
                icon = Icons.Default.HelpOutline,
                iconTint = primaryColor,
                iconBg = primaryColor.copy(alpha = 0.12f),
                title = "Help & Support",
                subtitle = "FAQ, Zero-Knowledge security guide & contact options",
                onClick = { onNavigateCategory(SettingsCategory.HELP_SUPPORT) },
                testTag = "settings_cat_help"
            )

            SettingsItemCard(
                icon = Icons.Default.Info,
                iconTint = primaryColor,
                iconBg = primaryColor.copy(alpha = 0.12f),
                title = "About Adera",
                subtitle = "Version 1.0.0, zero-knowledge architecture & terms",
                onClick = { onNavigateCategory(SettingsCategory.ABOUT) },
                testTag = "settings_cat_about"
            )

            // Categories Section 4: Danger Zone
            SettingsSectionTitle("Danger Zone")

            SettingsItemCard(
                icon = Icons.Default.DeleteForever,
                iconTint = HealthRiskRed,
                iconBg = HealthRiskRed.copy(alpha = 0.12f),
                title = "Danger Zone",
                subtitle = "Permanently delete all encrypted vault data & factory reset",
                onClick = { onNavigateCategory(SettingsCategory.DANGER_ZONE) },
                testTag = "settings_cat_danger"
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
