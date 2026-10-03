package com.example.adere.presentation.screens.settings

import android.widget.Toast
import androidx.biometric.BiometricManager
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
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
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsOptionDialog
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.adere.presentation.screens.settings.components.SettingsValueItem
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed

fun formatAutoLockLabel(seconds: Int): String {
    return when (seconds) {
        0 -> "Immediately"
        30 -> "30 seconds"
        60 -> "1 minute"
        300 -> "5 minutes"
        900 -> "15 minutes"
        3600 -> "1 hour"
        -1 -> "Never"
        else -> "$seconds seconds"
    }
}

@Composable
fun SecuritySettingsScreen(
    biometricEnabled: Boolean,
    autoLockSeconds: Int,
    onToggleBiometric: (Boolean) -> Unit,
    onSetAutoLockSeconds: (Int) -> Unit,
    onChangeMasterPassword: (old: String, new: String, (Result<Unit>) -> Unit) -> Unit,
    onGetRecoveryKey: () -> Result<String>,
    onRegenerateRecoveryKey: ((Result<String>) -> Unit) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showAutoLockDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showRecoveryKeyDialog by remember { mutableStateOf(false) }
    var showRegenerateConfirmDialog by remember { mutableStateOf(false) }
    var currentRecoveryKey by remember { mutableStateOf("") }

    // Check biometric hardware availability
    val biometricManager = remember { BiometricManager.from(context) }
    val canAuth = remember { biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Security & Master Key",
                subtitle = "Biometrics, Auto-Lock, Master Password & Recovery",
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
            // Section 1: Biometric Authentication
            SettingsSectionTitle("Biometric Authentication")

            val biometricSubtitle = when (canAuth) {
                BiometricManager.BIOMETRIC_SUCCESS -> "Unlock vault DEK using Fingerprint / Face ID"
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometrics enrolled on device. Set up fingerprint in Android Settings."
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "Biometric hardware not available on this device."
                else -> "Biometric authentication currently unavailable."
            }

            SettingsSwitchItem(
                icon = Icons.Default.Fingerprint,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Biometric Decryption",
                subtitle = biometricSubtitle,
                checked = biometricEnabled && (canAuth == BiometricManager.BIOMETRIC_SUCCESS),
                onCheckedChange = { enable ->
                    if (enable && canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
                        val msg = if (canAuth == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                            "Please enroll a fingerprint or face unlock in Android Settings first."
                        } else {
                            "Biometrics are not supported or available on this device."
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } else {
                        onToggleBiometric(enable)
                    }
                },
                testTag = "settings_switch_biometric"
            )

            // Section 2: Session Protection & Inactivity Timers
            SettingsSectionTitle("Session Protection & Inactivity")

            SettingsValueItem(
                icon = Icons.Default.Timer,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Auto-Lock Inactivity Timer",
                subtitle = "Purge DEK from device memory after inactive interval",
                valueText = formatAutoLockLabel(autoLockSeconds),
                onClick = { showAutoLockDialog = true },
                testTag = "settings_autolock_item"
            )

            // Section 3: Master Password & Key Material
            SettingsSectionTitle("Master Password & Emergency Recovery")

            SettingsItemCard(
                icon = Icons.Default.LockReset,
                iconTint = EmeraldLight,
                iconBg = EmeraldLight.copy(alpha = 0.15f),
                title = "Change Master Password",
                subtitle = "Re-encrypt vault DEK with a new passphrase",
                onClick = { showChangePasswordDialog = true },
                testTag = "settings_change_password_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Key,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Master Recovery Key",
                subtitle = "View or regenerate your 32-character offline recovery key",
                onClick = {
                    val res = onGetRecoveryKey()
                    if (res.isSuccess) {
                        currentRecoveryKey = res.getOrDefault("")
                        showRecoveryKeyDialog = true
                    } else {
                        Toast.makeText(context, "Failed: ${res.exceptionOrNull()?.message ?: "Vault locked"}", Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "settings_recovery_key_item"
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    // Modal 1: Auto-Lock Options Dialog
    if (showAutoLockDialog) {
        val autoLockOptions = listOf(
            0 to "Immediately",
            30 to "30 seconds",
            60 to "1 minute",
            300 to "5 minutes",
            900 to "15 minutes",
            3600 to "1 hour"
        )
        SettingsOptionDialog(
            title = "Select Auto-Lock Timer",
            options = autoLockOptions,
            selectedOption = autoLockSeconds,
            onSelectOption = { seconds -> onSetAutoLockSeconds(seconds) },
            onDismiss = { showAutoLockDialog = false }
        )
    }

    // Modal 2: Change Master Password Dialog Flow
    if (showChangePasswordDialog) {
        var currentPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var currentVisible by remember { mutableStateOf(false) }
        var newVisible by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        var isSubmitting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showChangePasswordDialog = false },
            title = {
                Text(
                    text = "Change Master Password",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Re-encrypts your Vault Data Encryption Key (DEK). Your stored credentials remain completely safe.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    if (errorMsg != null) {
                        Text(
                            text = errorMsg!!,
                            color = HealthRiskRed,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedTextField(
                        value = currentPass,
                        onValueChange = { currentPass = it; errorMsg = null },
                        label = { Text("Current Password") },
                        singleLine = true,
                        visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { currentVisible = !currentVisible }) {
                                Icon(
                                    imageVector = if (currentVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("input_current_password")
                    )

                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it; errorMsg = null },
                        label = { Text("New Password (min 8 chars)") },
                        singleLine = true,
                        visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { newVisible = !newVisible }) {
                                Icon(
                                    imageVector = if (newVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("input_new_password")
                    )

                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it; errorMsg = null },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("input_confirm_password")
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = !isSubmitting,
                    onClick = {
                        if (currentPass.isBlank()) {
                            errorMsg = "Please enter your current master password"
                            return@Button
                        }
                        if (newPass.length < 8) {
                            errorMsg = "New password must be at least 8 characters"
                            return@Button
                        }
                        if (newPass != confirmPass) {
                            errorMsg = "New passwords do not match"
                            return@Button
                        }

                        isSubmitting = true
                        onChangeMasterPassword(currentPass, newPass) { result ->
                            isSubmitting = false
                            if (result.isSuccess) {
                                showChangePasswordDialog = false
                                Toast.makeText(context, "Master Password changed successfully!", Toast.LENGTH_LONG).show()
                            } else {
                                errorMsg = result.exceptionOrNull()?.message ?: "Incorrect current password or verification failed"
                            }
                        }
                    }
                ) {
                    Text(if (isSubmitting) "Updating..." else "Update Password")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isSubmitting,
                    onClick = { showChangePasswordDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal 3: View Recovery Key Dialog
    if (showRecoveryKeyDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryKeyDialog = false },
            title = {
                Text(
                    text = "Master Recovery Key",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Keep this offline emergency key written down safely. It can recover your entire vault if you forget your master password.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentRecoveryKey,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(currentRecoveryKey))
                                    Toast.makeText(context, "Recovery key copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Recovery Key",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showRecoveryKeyDialog = false }) {
                    Text("Done")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showRecoveryKeyDialog = false
                        showRegenerateConfirmDialog = true
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Regenerate")
                    }
                }
            }
        )
    }

    // Modal 4: Regenerate Recovery Key Confirmation
    if (showRegenerateConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRegenerateConfirmDialog = false },
            title = { Text("Regenerate Recovery Key?") },
            text = {
                Text("Generating a new recovery key invalidates your old key. Any written physical copies of your old key will no longer work.")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRiskRed),
                    onClick = {
                        showRegenerateConfirmDialog = false
                        onRegenerateRecoveryKey { res ->
                            if (res.isSuccess) {
                                currentRecoveryKey = res.getOrDefault("")
                                showRecoveryKeyDialog = true
                                Toast.makeText(context, "New Master Recovery Key generated!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Failed to regenerate key", Toast.LENGTH_SHORT).show()
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
}
