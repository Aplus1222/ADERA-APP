package com.example.adere.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.presentation.screens.settings.AboutScreen
import com.example.adere.presentation.screens.settings.AppearanceSettingsScreen
import com.example.adere.presentation.screens.settings.BackupRestoreScreen
import com.example.adere.presentation.screens.settings.DangerZoneScreen
import com.example.adere.presentation.screens.settings.GeneratorSettingsScreen
import com.example.adere.presentation.screens.settings.HelpSupportScreen
import com.example.adere.presentation.screens.settings.NotificationSettingsScreen
import com.example.adere.presentation.screens.settings.PrivacySettingsScreen
import com.example.adere.presentation.screens.settings.SecuritySettingsScreen
import com.example.adere.presentation.screens.settings.SettingsCategory
import com.example.adere.presentation.screens.settings.SettingsMainScreen
import com.example.ui.theme.VaultThemePalette

@Composable
fun SettingsScreen(
    totalCredentialsCount: Int = 0,
    favoritesCount: Int = 0,
    isVaultProtected: Boolean = true,
    biometricEnabled: Boolean = false,
    autoLockSeconds: Int = 60,
    screenshotProtection: Boolean = false,
    clipboardClearSeconds: Int = 30,
    hideRecentAppsContent: Boolean = false,
    notificationsEnabled: Boolean = true,
    themePalette: VaultThemePalette = VaultThemePalette.TOTAL_SECURITY,
    generatorOptions: PasswordGenerator.GeneratorOptions = PasswordGenerator.GeneratorOptions(),
    onSelectThemePalette: (VaultThemePalette) -> Unit = {},
    onToggleBiometric: (Boolean) -> Unit = {},
    onSetAutoLockSeconds: (Int) -> Unit = {},
    onToggleScreenshotProtection: (Boolean) -> Unit = {},
    onSetClipboardClearSeconds: (Int) -> Unit = {},
    onToggleHideRecentApps: (Boolean) -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {},
    onUpdateGeneratorOptions: (PasswordGenerator.GeneratorOptions) -> Unit = {},
    onChangeMasterPassword: (old: String, new: String, (Result<Unit>) -> Unit) -> Unit = { _, _, cb -> cb(Result.success(Unit)) },
    onExportBackup: (passphrase: String, (Result<String>) -> Unit) -> Unit = { _, cb -> cb(Result.success("")) },
    onRestoreBackup: (content: String, passphrase: String, (Result<Int>) -> Unit) -> Unit = { _, _, cb -> cb(Result.success(0)) },
    onExportPdf: (options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit = { _, _ -> },
    onGetRecoveryKey: () -> Result<String> = { Result.failure(IllegalStateException()) },
    onRegenerateRecoveryKey: ((Result<String>) -> Unit) -> Unit = {},
    onResetVault: () -> Unit = {}
) {
    var activeCategory by remember { mutableStateOf<SettingsCategory?>(null) }

    if (activeCategory != null) {
        BackHandler {
            activeCategory = null
        }
    }

    when (activeCategory) {
        null -> {
            SettingsMainScreen(
                totalCredentialsCount = totalCredentialsCount,
                favoritesCount = favoritesCount,
                isVaultProtected = isVaultProtected,
                onNavigateCategory = { cat -> activeCategory = cat }
            )
        }

        SettingsCategory.SECURITY -> {
            SecuritySettingsScreen(
                biometricEnabled = biometricEnabled,
                autoLockSeconds = autoLockSeconds,
                onToggleBiometric = onToggleBiometric,
                onSetAutoLockSeconds = onSetAutoLockSeconds,
                onChangeMasterPassword = onChangeMasterPassword,
                onGetRecoveryKey = onGetRecoveryKey,
                onRegenerateRecoveryKey = onRegenerateRecoveryKey,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.PRIVACY -> {
            PrivacySettingsScreen(
                screenshotProtection = screenshotProtection,
                clipboardClearSeconds = clipboardClearSeconds,
                hideRecentAppsContent = hideRecentAppsContent,
                onToggleScreenshotProtection = onToggleScreenshotProtection,
                onSetClipboardClearSeconds = onSetClipboardClearSeconds,
                onToggleHideRecentApps = onToggleHideRecentApps,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.BACKUP_RESTORE -> {
            BackupRestoreScreen(
                onExportBackup = onExportBackup,
                onRestoreBackup = onRestoreBackup,
                onExportPdf = onExportPdf,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.APPEARANCE -> {
            AppearanceSettingsScreen(
                currentPalette = themePalette,
                onSelectThemePalette = onSelectThemePalette,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.GENERATOR -> {
            GeneratorSettingsScreen(
                generatorOptions = generatorOptions,
                onUpdateGeneratorOptions = onUpdateGeneratorOptions,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.NOTIFICATIONS -> {
            NotificationSettingsScreen(
                notificationsEnabled = notificationsEnabled,
                onToggleNotifications = onToggleNotifications,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.HELP_SUPPORT -> {
            HelpSupportScreen(
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.ABOUT -> {
            AboutScreen(
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.DANGER_ZONE -> {
            DangerZoneScreen(
                onResetVault = onResetVault,
                onBackClick = { activeCategory = null }
            )
        }
    }
}
