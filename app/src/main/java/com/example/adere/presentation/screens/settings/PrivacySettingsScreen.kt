package com.example.adere.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ContentPasteGo
import androidx.compose.material.icons.filled.ImportantDevices
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.screens.settings.components.SettingsOptionDialog
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.adere.presentation.screens.settings.components.SettingsValueItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.TotalSecurityPrimary

fun formatClipboardClearLabel(seconds: Int): String {
    return when (seconds) {
        0 -> "Disabled"
        10 -> "10 seconds"
        30 -> "30 seconds"
        60 -> "60 seconds"
        else -> "$seconds seconds"
    }
}

@Composable
fun PrivacySettingsScreen(
    screenshotProtection: Boolean,
    clipboardClearSeconds: Int,
    hideRecentAppsContent: Boolean,
    onToggleScreenshotProtection: (Boolean) -> Unit,
    onSetClipboardClearSeconds: (Int) -> Unit,
    onToggleHideRecentApps: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    var showClipboardDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Privacy & Screen Protection",
                subtitle = "Screenshot protection, Clipboard auto-clear & Recent Apps",
                onBackClick = onBackClick
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
            // Section 1: Screen & Capture Protection
            SettingsSectionTitle("Screen & Task Switcher Protection")

            SettingsSwitchItem(
                icon = Icons.Default.ScreenLockPortrait,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.12f),
                title = "Anti-Screenshot Protection",
                subtitle = "Enforce FLAG_SECURE against screenshots, screen recording & task switcher previews",
                checked = screenshotProtection,
                onCheckedChange = onToggleScreenshotProtection,
                testTag = "settings_switch_screenshot"
            )

            SettingsSwitchItem(
                icon = Icons.Default.ImportantDevices,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Hide App Content in Recent Apps",
                subtitle = "Blurs or hides sensitive vault UI when switching between Android applications",
                checked = hideRecentAppsContent,
                onCheckedChange = onToggleHideRecentApps,
                testTag = "settings_switch_recent_apps"
            )

            // Section 2: Clipboard Auto-Clear
            SettingsSectionTitle("System Clipboard Safety")

            SettingsValueItem(
                icon = Icons.Default.ContentPasteGo,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Clipboard Auto-Clear Interval",
                subtitle = "Automatically purges copied secrets from system clipboard to prevent leakages",
                valueText = formatClipboardClearLabel(clipboardClearSeconds),
                onClick = { showClipboardDialog = true },
                testTag = "settings_clipboard_item"
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    // Modal: Clipboard Clear Duration Options
    if (showClipboardDialog) {
        val clipboardOptions = listOf(
            10 to "10 seconds",
            30 to "30 seconds",
            60 to "60 seconds",
            0 to "Disabled"
        )
        SettingsOptionDialog(
            title = "Select Clipboard Auto-Clear Duration",
            options = clipboardOptions,
            selectedOption = clipboardClearSeconds,
            onSelectOption = { seconds -> onSetClipboardClearSeconds(seconds) },
            onDismiss = { showClipboardDialog = false }
        )
    }
}
