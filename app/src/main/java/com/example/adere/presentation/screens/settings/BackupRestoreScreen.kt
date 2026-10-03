package com.example.adere.presentation.screens.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.backup.BackupManager
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.TotalSecurityPrimary

@Composable
fun BackupRestoreScreen(
    onExportBackup: (passphrase: String, (Result<String>) -> Unit) -> Unit,
    onRestoreBackup: (content: String, passphrase: String, (Result<Int>) -> Unit) -> Unit,
    onExportPdf: (options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit,
    onGetRecoveryKey: (() -> Result<String>)? = null,
    onRegenerateRecoveryKey: (((Result<String>) -> Unit) -> Unit)? = null,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showExportDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showPdfOptionsDialog by remember { mutableStateOf(false) }
    var showExportResultDialog by remember { mutableStateOf<String?>(null) }
    var showRecoveryKeyDialog by remember { mutableStateOf(false) }
    var showRegenerateConfirmDialog by remember { mutableStateOf(false) }
    var currentRecoveryKey by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Backup & Vault Recovery",
                subtitle = "Zero-knowledge backups, Emergency Master Recovery Key & PDF archive",
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
            // Status Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Zero-Knowledge Protection Active",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "AES-256-GCM encryption • Argon2id key derivation • Offline first",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Section 1: Encrypted Vault Backups (.adere)
            SettingsSectionTitle("Encrypted Vault Backups (.adere)")

            SettingsItemCard(
                icon = Icons.Default.Upload,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Create Encrypted Backup",
                subtitle = "Export all vault items securely into an offline passphrase-protected .adere package",
                onClick = { showExportDialog = true },
                testTag = "settings_backup_export_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Download,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Restore & Verify Vault Data",
                subtitle = "Decrypt and preview an .adere backup package before merging",
                onClick = { showRestoreDialog = true },
                testTag = "settings_backup_restore_item"
            )

            // Section 2: Master Recovery Key (Emergency Vault Access)
            SettingsSectionTitle("Master Recovery Key (Emergency)")

            SettingsItemCard(
                icon = Icons.Default.Key,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "View Master Recovery Key",
                subtitle = "Display your 24-character emergency recovery key for safe paper archiving",
                onClick = {
                    if (onGetRecoveryKey != null) {
                        val res = onGetRecoveryKey()
                        if (res.isSuccess) {
                            currentRecoveryKey = res.getOrDefault("")
                            showRecoveryKeyDialog = true
                        } else {
                            Toast.makeText(context, res.exceptionOrNull()?.message ?: "Unlock vault first to view recovery key", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Recovery key access unavailable", Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "settings_view_recovery_key"
            )

            SettingsItemCard(
                icon = Icons.Default.Refresh,
                iconTint = HealthRiskRed,
                iconBg = HealthRiskRed.copy(alpha = 0.12f),
                title = "Regenerate Recovery Key",
                subtitle = "Invalidate old emergency key and generate a fresh recovery key",
                onClick = { showRegenerateConfirmDialog = true },
                testTag = "settings_regenerate_recovery_key"
            )

            // Section 3: Printable & Shareable PDF Archive
            SettingsSectionTitle("Printable & Shareable PDF Archive")

            SettingsItemCard(
                icon = Icons.Default.PictureAsPdf,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Export Vault to PDF Document",
                subtitle = "Configure and save executive color-coded PDF master sheet for cold storage",
                onClick = { showPdfOptionsDialog = true },
                testTag = "settings_export_pdf_save"
            )

            SettingsItemCard(
                icon = Icons.Default.Share,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Share Vault PDF Sheet",
                subtitle = "Securely generate and open system file chooser to transmit or store PDF archive",
                onClick = {
                    onExportPdf(
                        PdfExportManager.ExportOptions(
                            includePasswords = true,
                            includeCryptoSecrets = true,
                            includeNotes = true,
                            includeRecoveryCodes = true
                        ),
                        true
                    )
                },
                testTag = "settings_export_pdf_share"
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    // Modal 1: Create Encrypted Backup Passphrase Dialog
    if (showExportDialog) {
        var passphrase by remember { mutableStateOf("") }
        var confirmPassphrase by remember { mutableStateOf("") }
        var isVisible by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        var isExporting by remember { mutableStateOf(false) }

        val strengthScore = remember(passphrase) {
            var score = 0
            if (passphrase.length >= 6) score++
            if (passphrase.length >= 10) score++
            if (passphrase.any { it.isDigit() }) score++
            if (passphrase.any { it.isUpperCase() } && passphrase.any { it.isLowerCase() }) score++
            if (passphrase.any { !it.isLetterOrDigit() }) score++
            score
        }

        AlertDialog(
            onDismissRequest = { if (!isExporting) showExportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Create Encrypted Backup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Choose a strong backup passphrase. This passphrase is required to decrypt and restore your .adere package on any device.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    if (errorMsg != null) {
                        Surface(
                            color = HealthRiskRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMsg!!,
                                color = HealthRiskRed,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = passphrase,
                        onValueChange = { passphrase = it; errorMsg = null },
                        label = { Text("Backup Passphrase (min 6 chars)") },
                        singleLine = true,
                        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isVisible = !isVisible }) {
                                Icon(
                                    imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("backup_passphrase_input")
                    )

                    if (passphrase.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { (strengthScore / 5f).coerceIn(0.1f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = when {
                                    strengthScore <= 2 -> HealthRiskRed
                                    strengthScore <= 4 -> TotalSecurityPrimary
                                    else -> EmeraldPrimary
                                }
                            )
                            Text(
                                text = when {
                                    strengthScore <= 2 -> "Passphrase Strength: Weak"
                                    strengthScore <= 4 -> "Passphrase Strength: Good"
                                    else -> "Passphrase Strength: Excellent"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = when {
                                        strengthScore <= 2 -> HealthRiskRed
                                        strengthScore <= 4 -> TotalSecurityPrimary
                                        else -> EmeraldPrimary
                                    }
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = confirmPassphrase,
                        onValueChange = { confirmPassphrase = it; errorMsg = null },
                        label = { Text("Confirm Backup Passphrase") },
                        singleLine = true,
                        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("backup_confirm_passphrase_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = !isExporting,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    onClick = {
                        if (passphrase.length < 6) {
                            errorMsg = "Passphrase must be at least 6 characters long."
                            return@Button
                        }
                        if (passphrase != confirmPassphrase) {
                            errorMsg = "Passphrases do not match."
                            return@Button
                        }

                        isExporting = true
                        onExportBackup(passphrase) { result ->
                            isExporting = false
                            if (result.isSuccess) {
                                showExportDialog = false
                                showExportResultDialog = result.getOrThrow()
                            } else {
                                errorMsg = result.exceptionOrNull()?.message ?: "Backup export failed."
                            }
                        }
                    }
                ) {
                    Text(if (isExporting) "Encrypting Package..." else "Create & Export")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isExporting,
                    onClick = { showExportDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal 2: Backup Success & Share/Copy Result Dialog
    showExportResultDialog?.let { backupString ->
        AlertDialog(
            onDismissRequest = { showExportResultDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Backup Package Created")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Your zero-knowledge encrypted .adere backup package is ready. Store this string securely or share it.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = backupString.take(120) + "...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    onClick = {
                        clipboardManager.setText(AnnotatedString(backupString))
                        Toast.makeText(context, "Encrypted backup string copied to clipboard!", Toast.LENGTH_LONG).show()
                        showExportResultDialog = null
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy String")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        try {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Adera Vault Encrypted Backup")
                                putExtra(Intent.EXTRA_TEXT, backupString)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Backup"))
                            showExportResultDialog = null
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to share backup", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                }
            }
        )
    }

    // Modal 3: Restore Vault with Two-Step Preview Flow
    if (showRestoreDialog) {
        var backupPayload by remember { mutableStateOf("") }
        var passphrase by remember { mutableStateOf("") }
        var isVisible by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        var isRestoring by remember { mutableStateOf(false) }
        var restorePreview by remember { mutableStateOf<BackupManager.RestorePreview?>(null) }

        AlertDialog(
            onDismissRequest = { if (!isRestoring) showRestoreDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = TotalSecurityPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restore Vault Backup")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (restorePreview == null) {
                        Text(
                            text = "Paste your encrypted .adere backup payload string and enter the backup passphrase to verify and restore your items.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        if (errorMsg != null) {
                            Surface(
                                color = HealthRiskRed.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMsg!!,
                                    color = HealthRiskRed,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = backupPayload,
                            onValueChange = { backupPayload = it; errorMsg = null },
                            label = { Text("Encrypted .adere Payload String") },
                            maxLines = 4,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val clipText = clipboardManager.getText()?.text
                                        if (!clipText.isNullOrBlank()) {
                                            backupPayload = clipText
                                            errorMsg = null
                                            Toast.makeText(context, "Pasted backup from clipboard", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Paste from Clipboard",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("restore_payload_input")
                        )

                        OutlinedTextField(
                            value = passphrase,
                            onValueChange = { passphrase = it; errorMsg = null },
                            label = { Text("Backup Passphrase") },
                            singleLine = true,
                            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { isVisible = !isVisible }) {
                                    Icon(
                                        imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("restore_passphrase_input")
                        )
                    } else {
                        // Preview state before final commit
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Backup Verified Successfully!",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                        )
                                    }
                                    Text(
                                        text = "Format Version: ${restorePreview!!.version} • Total Secrets: ${restorePreview!!.itemCount}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                    )
                                }
                            }

                            Text(
                                text = "Preview of items to be restored and merged into your vault:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth().height(140.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp).verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    for (item in restorePreview!!.items) {
                                        Text(
                                            text = "• [${item.category.name}] ${item.title}${if (item.username.isNotBlank()) " (${item.username})" else ""}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isRestoring,
                    colors = ButtonDefaults.buttonColors(containerColor = TotalSecurityPrimary),
                    onClick = {
                        if (restorePreview == null) {
                            val cleanPayload = backupPayload.trim()
                            if (cleanPayload.isBlank()) {
                                errorMsg = "Please paste the backup payload string."
                                return@Button
                            }
                            if (passphrase.isBlank()) {
                                errorMsg = "Please enter the backup passphrase."
                                return@Button
                            }

                            // Step 1: Decrypt and preview locally using BackupManager
                            val previewResult = BackupManager.decryptAndValidateBackup(cleanPayload, passphrase.toCharArray())
                            if (previewResult.isSuccess) {
                                restorePreview = previewResult.getOrThrow()
                                errorMsg = null
                            } else {
                                errorMsg = previewResult.exceptionOrNull()?.message ?: "Incorrect passphrase or invalid backup format."
                            }
                        } else {
                            // Step 2: Final commit restore via repository
                            isRestoring = true
                            onRestoreBackup(backupPayload.trim(), passphrase) { result ->
                                isRestoring = false
                                if (result.isSuccess) {
                                    showRestoreDialog = false
                                    val count = result.getOrDefault(0)
                                    Toast.makeText(context, "Successfully restored and merged $count vault items!", Toast.LENGTH_LONG).show()
                                } else {
                                    errorMsg = result.exceptionOrNull()?.message ?: "Restore failed."
                                    restorePreview = null
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        when {
                            isRestoring -> "Restoring Vault..."
                            restorePreview == null -> "Verify & Preview Backup"
                            else -> "Confirm & Restore Items"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isRestoring,
                    onClick = { showRestoreDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal 4: View Master Recovery Key Dialog
    if (showRecoveryKeyDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryKeyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = TotalSecurityPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Master Recovery Key")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Your Master Recovery Key allows zero-knowledge vault restoration if you ever forget your Master Password. Keep this key in a secure physical location.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentRecoveryKey,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Surface(
                        color = HealthRiskRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Warning, contentDescription = null, tint = HealthRiskRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Never share your recovery key. Adere staff will never ask for it.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = HealthRiskRed)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(currentRecoveryKey))
                        Toast.makeText(context, "Master Recovery Key copied to clipboard!", Toast.LENGTH_LONG).show()
                        showRecoveryKeyDialog = false
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Key")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoveryKeyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal 5: Regenerate Recovery Key Confirmation Dialog
    if (showRegenerateConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRegenerateConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Warning, contentDescription = null, tint = HealthRiskRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Regenerate Recovery Key?")
                }
            },
            text = {
                Text(
                    text = "Regenerating your Master Recovery Key will immediately invalidate your old emergency key. Make sure to back up the new key immediately.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRiskRed),
                    onClick = {
                        showRegenerateConfirmDialog = false
                        if (onRegenerateRecoveryKey != null) {
                            onRegenerateRecoveryKey { res ->
                                if (res.isSuccess) {
                                    currentRecoveryKey = res.getOrDefault("")
                                    showRecoveryKeyDialog = true
                                    Toast.makeText(context, "New Master Recovery Key generated successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Regenerate Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegenerateConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal 6: PDF Export Customization Options Dialog
    if (showPdfOptionsDialog) {
        var includePasswords by remember { mutableStateOf(true) }
        var includeCrypto by remember { mutableStateOf(true) }
        var includeNotes by remember { mutableStateOf(true) }
        var includeRecovery by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showPdfOptionsDialog = false },
            title = {
                Text(
                    text = "Configure PDF Master Sheet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select which secure fields to include in the exported executive PDF document:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includePasswords = !includePasswords }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includePasswords,
                            onCheckedChange = { includePasswords = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Include Passwords & PINs", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeCrypto = !includeCrypto }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeCrypto,
                            onCheckedChange = { includeCrypto = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Include Crypto Secrets & Seed Phrases", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeNotes = !includeNotes }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeNotes,
                            onCheckedChange = { includeNotes = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Include Secure Notes", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeRecovery = !includeRecovery }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeRecovery,
                            onCheckedChange = { includeRecovery = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Include Emergency Recovery Codes", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPdfOptionsDialog = false
                        onExportPdf(
                            PdfExportManager.ExportOptions(
                                includePasswords = includePasswords,
                                includeCryptoSecrets = includeCrypto,
                                includeNotes = includeNotes,
                                includeRecoveryCodes = includeRecovery
                            ),
                            false
                        )
                    }
                ) {
                    Text("Save PDF Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPdfOptionsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
