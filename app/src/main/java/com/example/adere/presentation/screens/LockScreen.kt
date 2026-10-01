package com.example.adere.presentation.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CleanBg
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.CleanSurface
import com.example.ui.theme.CleanSurfaceVariant
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TotalSecurityPrimary
import com.example.ui.theme.TotalSecurityPrimaryContainer

@Composable
fun LockScreen(
    isBiometricAvailable: Boolean,
    onTriggerBiometrics: () -> Unit,
    onUnlockWithPassword: (String, (Result<Unit>) -> Unit) -> Unit,
    onRecoverWithKey: (recoveryKey: String, newPassword: String, (Result<Unit>) -> Unit) -> Unit = { _, _, _ -> },
    onResetVault: () -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    var showRecoveryDialog by remember { mutableStateOf(false) }
    var recoveryKeyInput by remember { mutableStateOf("") }
    var recoveryNewPassword by remember { mutableStateOf("") }
    var recoveryConfirmPassword by remember { mutableStateOf("") }
    var recoveryPasswordVisible by remember { mutableStateOf(false) }
    var recoveryErrorMessage by remember { mutableStateOf<String?>(null) }
    var isRecovering by remember { mutableStateOf(false) }

    if (isBiometricAvailable) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            onTriggerBiometrics()
        }
    }

    fun submitPassword() {
        val trimmed = password.trim()
        if (trimmed.isBlank()) {
            errorMessage = "Please enter your master password."
            return
        }
        isAuthenticating = true
        errorMessage = null
        onUnlockWithPassword(trimmed) { result ->
            isAuthenticating = false
            if (result.isFailure) {
                errorMessage = result.exceptionOrNull()?.message ?: "Incorrect master password."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Vault Lock Emblem with new App Logo
        Box(
            modifier = Modifier
                .size(88.dp)
                .shadow(12.dp, CircleShape, spotColor = TotalSecurityPrimary)
                .clip(CircleShape)
                .background(TotalSecurityPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Total Security Logo",
                modifier = Modifier.size(76.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Authentication Required",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextDarkPrimary,
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Fingerprint or face unlock required before viewing stored secrets",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkSecondary),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CleanSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (isBiometricAvailable) {
                    Button(
                        onClick = onTriggerBiometrics,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("unlock_biometric_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TotalSecurityPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Unlock with Fingerprint or Face",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(CleanBorder)
                        )
                        Text(
                            text = " OR MASTER PASSWORD ",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextDarkMuted, fontSize = 11.sp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(CleanBorder)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Master Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submitPassword() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = TextDarkSecondary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CleanSurfaceVariant,
                        unfocusedContainerColor = CleanSurfaceVariant,
                        focusedBorderColor = TotalSecurityPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextDarkPrimary,
                        unfocusedTextColor = TextDarkPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lock_screen_password_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage!!,
                        color = HealthRiskRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { submitPassword() },
                    enabled = !isAuthenticating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("lock_screen_unlock_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBiometricAvailable) CleanSurfaceVariant else TotalSecurityPrimary,
                        contentColor = if (isBiometricAvailable) TextDarkPrimary else Color.White
                    )
                ) {
                    if (isAuthenticating) {
                        CircularProgressIndicator(
                            color = TotalSecurityPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Unlock with Password",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        recoveryErrorMessage = null
                        recoveryKeyInput = ""
                        recoveryNewPassword = ""
                        recoveryConfirmPassword = ""
                        showRecoveryDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("lock_screen_recover_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TotalSecurityPrimary),
                    border = BorderStroke(1.dp, TotalSecurityPrimary.copy(alpha = 0.5f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Forgot Password? Use Recovery Key",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { showResetConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lock_screen_reset_vault_button")
                ) {
                    Text(
                        text = "Reset Vault (Wipes Data)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDarkMuted, fontSize = 11.5.sp)
                    )
                }
            }
        }

        // Master Recovery Key Dialog
        if (showRecoveryDialog) {
            AlertDialog(
                onDismissRequest = { if (!isRecovering) showRecoveryDialog = false },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(TotalSecurityPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = TotalSecurityPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Recover Encrypted Vault",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        )
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Enter your 24-character Master Recovery Key to restore your vault and set a new Master Password without losing your data.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                        )

                        OutlinedTextField(
                            value = recoveryKeyInput,
                            onValueChange = { input ->
                                // Auto uppercase and format
                                val raw = input.replace("-", "").replace(" ", "").uppercase()
                                recoveryKeyInput = raw.chunked(4).joinToString("-").take(29)
                                recoveryErrorMessage = null
                            },
                            label = { Text("Master Recovery Key") },
                            placeholder = { Text("XXXX-XXXX-XXXX-XXXX-XXXX-XXXX") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CleanSurfaceVariant,
                                unfocusedContainerColor = CleanSurfaceVariant,
                                focusedBorderColor = TotalSecurityPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextDarkPrimary,
                                unfocusedTextColor = TextDarkPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_key_input")
                        )

                        OutlinedTextField(
                            value = recoveryNewPassword,
                            onValueChange = {
                                recoveryNewPassword = it
                                recoveryErrorMessage = null
                            },
                            label = { Text("New Master Password") },
                            singleLine = true,
                            visualTransformation = if (recoveryPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { recoveryPasswordVisible = !recoveryPasswordVisible }) {
                                    Icon(
                                        imageVector = if (recoveryPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = null,
                                        tint = TextDarkSecondary
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CleanSurfaceVariant,
                                unfocusedContainerColor = CleanSurfaceVariant,
                                focusedBorderColor = TotalSecurityPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextDarkPrimary,
                                unfocusedTextColor = TextDarkPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_new_password_input")
                        )

                        OutlinedTextField(
                            value = recoveryConfirmPassword,
                            onValueChange = {
                                recoveryConfirmPassword = it
                                recoveryErrorMessage = null
                            },
                            label = { Text("Confirm New Password") },
                            singleLine = true,
                            visualTransformation = if (recoveryPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CleanSurfaceVariant,
                                unfocusedContainerColor = CleanSurfaceVariant,
                                focusedBorderColor = TotalSecurityPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextDarkPrimary,
                                unfocusedTextColor = TextDarkPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_confirm_password_input")
                        )

                        if (recoveryErrorMessage != null) {
                            Text(
                                text = recoveryErrorMessage!!,
                                color = HealthRiskRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cleanKey = recoveryKeyInput.replace("-", "").replace(" ", "").trim()
                            if (cleanKey.length != 24) {
                                recoveryErrorMessage = "Recovery key must be 24 characters long."
                                return@Button
                            }
                            val newPass = recoveryNewPassword.trim()
                            if (newPass.length < 8) {
                                recoveryErrorMessage = "New password must be at least 8 characters."
                                return@Button
                            }
                            if (newPass != recoveryConfirmPassword.trim()) {
                                recoveryErrorMessage = "Passwords do not match."
                                return@Button
                            }

                            isRecovering = true
                            recoveryErrorMessage = null
                            onRecoverWithKey(cleanKey, newPass) { res ->
                                isRecovering = false
                                if (res.isSuccess) {
                                    showRecoveryDialog = false
                                } else {
                                    recoveryErrorMessage = res.exceptionOrNull()?.message ?: "Recovery failed. Verify your recovery key."
                                }
                            }
                        },
                        enabled = !isRecovering,
                        modifier = Modifier.testTag("recovery_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TotalSecurityPrimary)
                    ) {
                        if (isRecovering) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Recover & Unlock", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showRecoveryDialog = false },
                        enabled = !isRecovering
                    ) {
                        Text("Cancel", color = TextDarkSecondary)
                    }
                },
                containerColor = CleanSurface,
                shape = RoundedCornerShape(18.dp)
            )
        }

        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                title = { Text("Reset Entire Vault?", color = HealthRiskRed, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Because this is an offline encrypted vault, forgotten master passwords cannot be recovered. Resetting will erase the current database and return to setup.\n\nDo you want to reset?",
                        color = TextDarkSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetConfirmDialog = false
                            onResetVault()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HealthRiskRed)
                    ) {
                        Text("Reset Vault", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmDialog = false }) {
                        Text("Cancel", color = TextDarkSecondary)
                    }
                },
                containerColor = CleanSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
