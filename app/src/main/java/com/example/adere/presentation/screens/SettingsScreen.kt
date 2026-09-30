package com.example.adere.presentation.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.presentation.components.AdereTopBar
import com.example.adere.presentation.components.SecurityWarningCard
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    biometricEnabled: Boolean,
    autoLockSeconds: Int,
    screenshotProtection: Boolean,
    clipboardClearSeconds: Int,
    themePalette: com.example.ui.theme.VaultThemePalette = com.example.ui.theme.VaultThemePalette.OBSIDIAN_EMERALD,
    onSelectThemePalette: (com.example.ui.theme.VaultThemePalette) -> Unit = {},
    onToggleBiometric: (Boolean) -> Unit,
    onSetAutoLockSeconds: (Int) -> Unit,
    onSetScreenshotProtection: (Boolean) -> Unit,
    onSetClipboardClearSeconds: (Int) -> Unit,
    onChangeMasterPassword: (old: String, new: String, (Result<Unit>) -> Unit) -> Unit,
    onExportBackup: (passphrase: String, (Result<String>) -> Unit) -> Unit,
    onRestoreBackup: (content: String, passphrase: String, (Result<Int>) -> Unit) -> Unit,
    onExportPdf: (options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit,
    onVerifyMasterPassword: (password: String, (Result<Unit>) -> Unit) -> Unit,
    onPanicLock: () -> Unit,
    onResetVault: () -> Unit
) {
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showExportPdfDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AdereTopBar(
                title = "Settings",
                subtitle = "Security & Storage Preferences",
                onLockClick = onPanicLock
            )
        },
        containerColor = CharcoalBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Security Controls Group
            Text(
                text = "Security & Authentication",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Biometric Unlock Switch
                    SettingsSwitchRow(
                        title = "Biometric Authentication",
                        description = "Unlock with fingerprint or face using Keystore",
                        icon = Icons.Default.Fingerprint,
                        checked = biometricEnabled,
                        onCheckedChange = onToggleBiometric
                    )

                    // Screenshot Protection Switch
                    SettingsSwitchRow(
                        title = "Screenshot Protection (FLAG_SECURE)",
                        description = "Block screenshots and app previews on physical hardware",
                        icon = Icons.Default.Security,
                        checked = screenshotProtection,
                        onCheckedChange = onSetScreenshotProtection
                    )

                    // Auto-lock Selector
                    SettingsClickableRow(
                        title = "Auto-Lock Timeout",
                        value = when (autoLockSeconds) {
                            0 -> "Immediately"
                            30 -> "30 seconds"
                            60 -> "1 minute"
                            300 -> "5 minutes"
                            900 -> "15 minutes"
                            else -> "$autoLockSeconds seconds"
                        },
                        icon = Icons.Default.Timer,
                        onClick = {
                            // Cycle through values
                            val next = when (autoLockSeconds) {
                                0 -> 30
                                30 -> 60
                                60 -> 300
                                300 -> 900
                                else -> 0
                            }
                            onSetAutoLockSeconds(next)
                        }
                    )

                    // Clipboard Timeout Selector
                    SettingsClickableRow(
                        title = "Clipboard Auto-Clear",
                        value = when (clipboardClearSeconds) {
                            15 -> "15 seconds"
                            30 -> "30 seconds"
                            60 -> "60 seconds"
                            else -> "$clipboardClearSeconds seconds"
                        },
                        icon = Icons.Default.Lock,
                        onClick = {
                            val next = when (clipboardClearSeconds) {
                                15 -> 30
                                30 -> 60
                                else -> 15
                            }
                            onSetClipboardClearSeconds(next)
                        }
                    )

                    // Change Master Password
                    SettingsClickableRow(
                        title = "Change Master Password",
                        value = "Re-encrypts vault key",
                        icon = Icons.Default.Lock,
                        onClick = { showChangePasswordDialog = true }
                    )
                }
            }

            // Backup & Recovery Group
            Text(
                text = "Encrypted Backup & Recovery",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingsClickableRow(
                        title = "Create Encrypted Backup",
                        value = "Export protected .adere file",
                        icon = Icons.Default.Backup,
                        onClick = { showExportDialog = true }
                    )
                    SettingsClickableRow(
                        title = "Restore from Backup",
                        value = "Import verified .adere file",
                        icon = Icons.Default.Restore,
                        onClick = { showRestoreDialog = true }
                    )
                    SettingsClickableRow(
                        title = "Export Passwords to PDF",
                        value = "Printable emergency credential sheet",
                        icon = Icons.Default.PictureAsPdf,
                        onClick = { showExportPdfDialog = true }
                    )
                }
            }

            // Theme & Aesthetic Appearance
            Text(
                text = "Vault Theme & Aesthetic",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Cyber Security Palette",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Customize the ambient glow and accent tones across your encrypted vault.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (palette in com.example.ui.theme.VaultThemePalette.values()) {
                            val isSelected = themePalette == palette
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectThemePalette(palette) }
                                    .testTag("theme_picker_${palette.name.lowercase()}"),
                                colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
                                border = if (isSelected) {
                                    androidx.compose.foundation.BorderStroke(2.dp, palette.primaryColor)
                                } else {
                                    androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder)
                                }
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(palette.primaryColor)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = palette.title.substringBefore(" "),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) TextPrimary else TextSecondary
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // About & System Info
            Text(
                text = "About & Privacy",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SettingsClickableRow(
                        title = "About Adere",
                        value = "v1.0 (Zero-knowledge & Offline)",
                        icon = Icons.Default.Info,
                        onClick = { showAboutDialog = true }
                    )
                }
            }

            // Emergency / Danger Zone
            Text(
                text = "Emergency & Danger Zone",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SecurityRed
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SecurityRed.copy(alpha = 0.5f)))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onPanicLock,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("panic_lock_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecurityOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lock Vault Immediately", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("wipe_vault_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecurityRed.copy(alpha = 0.2f),
                            contentColor = SecurityRed
                        )
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Wipe & Reset Entire Vault", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Dialog: Change Master Password
        if (showChangePasswordDialog) {
            ChangePasswordDialog(
                onDismiss = { showChangePasswordDialog = false },
                onConfirm = { oldPass, newPass, cb ->
                    onChangeMasterPassword(oldPass, newPass) { res ->
                        if (res.isSuccess) {
                            showChangePasswordDialog = false
                        }
                        cb(res)
                    }
                }
            )
        }

        // Dialog: Export Backup
        if (showExportDialog) {
            ExportBackupDialog(
                onDismiss = { showExportDialog = false },
                onExport = onExportBackup
            )
        }

        // Dialog: Restore Backup
        if (showRestoreDialog) {
            RestoreBackupDialog(
                onDismiss = { showRestoreDialog = false },
                onRestore = onRestoreBackup
            )
        }

        // Dialog: Export Passwords to PDF
        if (showExportPdfDialog) {
            ExportPdfDialog(
                onDismiss = { showExportPdfDialog = false },
                onVerifyMasterPassword = onVerifyMasterPassword,
                onGeneratePdf = onExportPdf
            )
        }

        // Dialog: Reset Vault
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Wipe Vault Permanently?", color = SecurityRed, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "This will destroy all local encrypted records and cryptographic keys. This action CANNOT be undone without an encrypted backup file.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDialog = false
                            onResetVault()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecurityRed)
                    ) {
                        Text("Destroy Vault", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = CharcoalSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Dialog: About & Privacy
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("Adere — Password & Secret Manager", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Version 1.0 (Production Release)", color = EmeraldLight, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Adere is an offline-first, zero-knowledge encrypted vault. Your secrets never leave this device. The app requires zero network permissions, sends zero analytics, and runs authenticated AES-256-GCM and PBKDF2-HMAC-SHA256 cryptography.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("Close", color = EmeraldLight)
                    }
                },
                containerColor = CharcoalSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold))
            Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = CharcoalBg)
        )
    }
}

@Composable
private fun SettingsClickableRow(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold))
            Text(text = value, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (oldPass: String, newPass: String, (Result<Unit>) -> Unit) -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isBusy by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Master Password", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it; errorMsg = null },
                    label = { Text("Current Master Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it; errorMsg = null },
                    label = { Text("New Master Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it; errorMsg = null },
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg != null) {
                    Text(errorMsg!!, color = SecurityRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPass.length < 8) {
                        errorMsg = "New password must be at least 8 chars."
                        return@Button
                    }
                    if (newPass != confirmPass) {
                        errorMsg = "New passwords do not match."
                        return@Button
                    }
                    isBusy = true
                    onConfirm(oldPass, newPass) { res ->
                        isBusy = false
                        if (res.isFailure) {
                            errorMsg = res.exceptionOrNull()?.message ?: "Failed to change password."
                        }
                    }
                },
                enabled = !isBusy,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Update", color = Color(0xFF003824), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = CharcoalSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ExportBackupDialog(
    onDismiss: () -> Unit,
    onExport: (passphrase: String, (Result<String>) -> Unit) -> Unit
) {
    var passphrase by remember { mutableStateOf("") }
    var confirmPassphrase by remember { mutableStateOf("") }
    var exportResultString by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isBusy by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Encrypted Backup", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (exportResultString == null) {
                    Text(
                        "Set a strong backup passphrase. This file will be encrypted with AES-256-GCM. Keep this passphrase safe!",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = passphrase,
                        onValueChange = { passphrase = it; errorMsg = null },
                        label = { Text("Backup Passphrase") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassphrase,
                        onValueChange = { confirmPassphrase = it; errorMsg = null },
                        label = { Text("Confirm Backup Passphrase") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Encrypted Backup Created Successfully!", color = EmeraldLight, fontWeight = FontWeight.Bold)
                    Text("Backup Header: ${exportResultString!!.take(36)}...", color = TextSecondary)
                }
                if (errorMsg != null) {
                    Text(errorMsg!!, color = SecurityRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            if (exportResultString == null) {
                Button(
                    onClick = {
                        if (passphrase.length < 8) {
                            errorMsg = "Backup passphrase must be at least 8 characters."
                            return@Button
                        }
                        if (passphrase != confirmPassphrase) {
                            errorMsg = "Passphrases do not match."
                            return@Button
                        }
                        isBusy = true
                        onExport(passphrase) { res ->
                            isBusy = false
                            if (res.isSuccess) {
                                exportResultString = res.getOrNull()
                            } else {
                                errorMsg = res.exceptionOrNull()?.message ?: "Export failed."
                            }
                        }
                    },
                    enabled = !isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Generate Backup", color = Color(0xFF003824), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Done")
                }
            }
        },
        dismissButton = {
            if (exportResultString == null) {
                TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
            }
        },
        containerColor = CharcoalSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun RestoreBackupDialog(
    onDismiss: () -> Unit,
    onRestore: (content: String, passphrase: String, (Result<Int>) -> Unit) -> Unit
) {
    var backupString by remember { mutableStateOf("") }
    var passphrase by remember { mutableStateOf("") }
    var statusMsg by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var isBusy by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore From Backup", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!isSuccess) {
                    Text(
                        "Paste the encrypted .adere backup string and enter the passphrase used to encrypt it.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = backupString,
                        onValueChange = { backupString = it },
                        label = { Text("Encrypted Backup String") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = passphrase,
                        onValueChange = { passphrase = it },
                        label = { Text("Backup Passphrase") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(statusMsg ?: "Restored successfully!", color = EmeraldLight, fontWeight = FontWeight.Bold)
                }
                if (statusMsg != null && !isSuccess) {
                    Text(statusMsg!!, color = SecurityRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            if (!isSuccess) {
                Button(
                    onClick = {
                        if (backupString.isBlank() || passphrase.isBlank()) {
                            statusMsg = "Please provide both backup data and passphrase."
                            return@Button
                        }
                        isBusy = true
                        statusMsg = null
                        onRestore(backupString, passphrase) { res ->
                            isBusy = false
                            if (res.isSuccess) {
                                isSuccess = true
                                statusMsg = "Successfully decrypted and imported ${res.getOrNull()} items!"
                            } else {
                                statusMsg = res.exceptionOrNull()?.message ?: "Decryption failed."
                            }
                        }
                    },
                    enabled = !isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Decrypt & Import", color = Color(0xFF003824), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onDismiss) { Text("Done") }
            }
        },
        dismissButton = {
            if (!isSuccess) {
                TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
            }
        },
        containerColor = CharcoalSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ExportPdfDialog(
    onDismiss: () -> Unit,
    onVerifyMasterPassword: (String, (Result<Unit>) -> Unit) -> Unit,
    onGeneratePdf: (options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit
) {
    var masterPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var includePasswords by remember { mutableStateOf(true) }
    var includeCryptoSecrets by remember { mutableStateOf(false) }
    var includeNotes by remember { mutableStateOf(true) }
    var includeRecoveryCodes by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Passwords to PDF", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecurityWarningCard(
                    text = "CRITICAL WARNING: Exporting to PDF creates an unencrypted physical or digital document. Anyone with access to this file can view your plaintext passwords."
                )

                Text(
                    text = "Authenticate with your Master Password to proceed:",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )

                OutlinedTextField(
                    value = masterPassword,
                    onValueChange = { masterPassword = it; errorMsg = null },
                    label = { Text("Master Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = TextSecondary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("pdf_export_master_password_input")
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, color = SecurityRed, style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    text = "PDF Content Options:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Include Passwords & PINs", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                    Checkbox(
                        checked = includePasswords,
                        onCheckedChange = { includePasswords = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary, checkmarkColor = CharcoalBg)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Include Crypto Seeds & Keys", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                    Checkbox(
                        checked = includeCryptoSecrets,
                        onCheckedChange = { includeCryptoSecrets = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary, checkmarkColor = CharcoalBg)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Include Secure Notes", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                    Checkbox(
                        checked = includeNotes,
                        onCheckedChange = { includeNotes = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary, checkmarkColor = CharcoalBg)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Include Recovery Codes", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                    Checkbox(
                        checked = includeRecoveryCodes,
                        onCheckedChange = { includeRecoveryCodes = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary, checkmarkColor = CharcoalBg)
                    )
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (masterPassword.isBlank()) {
                            errorMsg = "Master password required."
                            return@Button
                        }
                        isVerifying = true
                        errorMsg = null
                        onVerifyMasterPassword(masterPassword) { result ->
                            isVerifying = false
                            if (result.isSuccess) {
                                val opts = PdfExportManager.ExportOptions(
                                    includePasswords = includePasswords,
                                    includeCryptoSecrets = includeCryptoSecrets,
                                    includeNotes = includeNotes,
                                    includeRecoveryCodes = includeRecoveryCodes
                                )
                                onDismiss()
                                onGeneratePdf(opts, false)
                            } else {
                                errorMsg = "Incorrect master password. PDF export aborted."
                            }
                        }
                    },
                    enabled = !isVerifying,
                    modifier = Modifier.fillMaxWidth().testTag("pdf_save_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color(0xFF003824))
                ) {
                    Text("Save PDF Document", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        if (masterPassword.isBlank()) {
                            errorMsg = "Master password required."
                            return@OutlinedButton
                        }
                        isVerifying = true
                        errorMsg = null
                        onVerifyMasterPassword(masterPassword) { result ->
                            isVerifying = false
                            if (result.isSuccess) {
                                val opts = PdfExportManager.ExportOptions(
                                    includePasswords = includePasswords,
                                    includeCryptoSecrets = includeCryptoSecrets,
                                    includeNotes = includeNotes,
                                    includeRecoveryCodes = includeRecoveryCodes
                                )
                                onDismiss()
                                onGeneratePdf(opts, true)
                            } else {
                                errorMsg = "Incorrect master password. PDF export aborted."
                            }
                        }
                    },
                    enabled = !isVerifying,
                    modifier = Modifier.fillMaxWidth().testTag("pdf_share_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldLight)
                ) {
                    Text("Print / Share PDF", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = CharcoalSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
