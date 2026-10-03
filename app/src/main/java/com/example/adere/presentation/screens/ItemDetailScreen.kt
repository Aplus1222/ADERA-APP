package com.example.adere.presentation.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.core.crypto.TOTPGenerator
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.presentation.components.VaultItemAvatar
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: VaultItem,
    totpResult: TOTPGenerator.TotpResult?,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onCopySecret: (String, String) -> Unit, // label, secret
    @Suppress("UNUSED_PARAMETER") onLockClick: () -> Unit = {},
    onExportPdf: ((options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit)? = null,
) {
    var isPasswordRevealed by remember { mutableStateOf(value = false) }
    var isSeedRevealed by remember { mutableStateOf(value = false) }
    var isPrivateKeyRevealed by remember { mutableStateOf(value = false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(value = false) }
    var showExportPdfDialog by remember { mutableStateOf(value = false) }

    // Auto-hide secrets after 30 seconds for shoulder-surfing protection
    LaunchedEffect(isPasswordRevealed) {
        if (isPasswordRevealed) {
            delay(30_000L)
            isPasswordRevealed = false
        }
    }
    LaunchedEffect(isSeedRevealed) {
        if (isSeedRevealed) {
            delay(30_000L)
            isSeedRevealed = false
        }
    }
    LaunchedEffect(isPrivateKeyRevealed) {
        if (isPrivateKeyRevealed) {
            delay(30_000L)
            isPrivateKeyRevealed = false
        }
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = item.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (onExportPdf != null) {
                        IconButton(
                            onClick = { showExportPdfDialog = true },
                            modifier = Modifier.testTag("detail_export_pdf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Export Emergency PDF Sheet",
                                tint = SecurityOrange
                            )
                        }
                    }
                    IconButton(onClick = { onToggleFavorite(item.id, item.isFavorite) }) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) GoldAccent else TextSecondary
                        )
                    }
                    IconButton(onClick = { onEditClick(item.id) }, modifier = Modifier.testTag("detail_edit_button")) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Item",
                            tint = EmeraldLight
                        )
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }, modifier = Modifier.testTag("detail_delete_button")) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Item",
                            tint = SecurityRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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

            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VaultItemAvatar(
                        title = item.title,
                        category = item.category,
                        url = item.payload.url,
                        size = 52.dp,
                        iconSize = 28.dp,
                        cornerRadius = 14.dp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.category.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldLight
                            )
                        )
                    }
                }
            }

            // Category Specific Fields
            when (item.category) {
                VaultCategory.CRYPTO -> {
                    // Crypto Seed Phrase & Private Key
                    if (item.payload.cryptoSeedPhrase.isNotBlank()) {
                        SecretFieldCard(
                            label = "Crypto Seed Phrase (12/24 Words)",
                            secretText = item.payload.cryptoSeedPhrase,
                            isRevealed = isSeedRevealed,
                            onToggleReveal = { isSeedRevealed = !isSeedRevealed },
                            onCopy = { onCopySecret("Seed Phrase", item.payload.cryptoSeedPhrase) },
                            testTag = "detail_copy_seed"
                        )
                    }

                    if (item.payload.cryptoPrivateKey.isNotBlank()) {
                        SecretFieldCard(
                            label = "Crypto Private Key",
                            secretText = item.payload.cryptoPrivateKey,
                            isRevealed = isPrivateKeyRevealed,
                            onToggleReveal = { isPrivateKeyRevealed = !isPrivateKeyRevealed },
                            onCopy = { onCopySecret("Private Key", item.payload.cryptoPrivateKey) },
                            testTag = "detail_copy_pk"
                        )
                    }

                    if (item.payload.cryptoAddress.isNotBlank()) {
                        PlainFieldCard(
                            label = "Public Wallet Address",
                            value = item.payload.cryptoAddress,
                            onCopy = { onCopySecret("Wallet Address", item.payload.cryptoAddress) },
                            testTag = "detail_copy_address"
                        )
                    }
                }

                VaultCategory.BANKING -> {
                    if (item.payload.pin.isNotBlank()) {
                        SecretFieldCard(
                            label = "Card / Bank PIN",
                            secretText = item.payload.pin,
                            isRevealed = isPasswordRevealed,
                            onToggleReveal = { isPasswordRevealed = !isPasswordRevealed },
                            onCopy = { onCopySecret("PIN", item.payload.pin) },
                            testTag = "detail_copy_pin"
                        )
                    }
                }

                else -> {}
            }

            // Username
            if (item.username.isNotBlank()) {
                PlainFieldCard(
                    label = "Username / Email / Account",
                    value = item.username,
                    onCopy = { onCopySecret("Username", item.username) },
                    testTag = "detail_copy_username"
                )
            }

            // Main Password
            if (item.payload.password.isNotBlank()) {
                SecretFieldCard(
                    label = "Password",
                    secretText = item.payload.password,
                    isRevealed = isPasswordRevealed,
                    onToggleReveal = { isPasswordRevealed = !isPasswordRevealed },
                    onCopy = { onCopySecret("Password", item.payload.password) },
                    testTag = "detail_copy_password"
                )
            }

            // URL Field
            if (item.payload.url.isNotBlank()) {
                PlainFieldCard(
                    label = "URL / Website",
                    value = item.payload.url,
                    onCopy = { onCopySecret("URL", item.payload.url) },
                    testTag = "detail_copy_url"
                )
            }

            // 2FA TOTP Card
            if (item.payload.totpSecret.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "2FA Authenticator Code",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            )
                            if (totpResult != null) {
                                Text(
                                    text = "${totpResult.secondsRemaining}s remaining",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        totpResult?.let { totp ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = totp.formattedCode,
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        letterSpacing = 2.sp
                                    )
                                )

                                IconButton(
                                    onClick = { onCopySecret("2FA Code", totp.code) },
                                    modifier = Modifier.testTag("detail_copy_totp_code")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy 2FA Code",
                                        tint = EmeraldLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            CircularProgressIndicator(
                                progress = { totp.progress },
                                modifier = Modifier.size(16.dp),
                                color = EmeraldPrimary,
                                trackColor = CharcoalSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Recovery Codes
            if (item.payload.recoveryCodes.isNotEmpty()) {
                PlainFieldCard(
                    label = "Recovery Codes",
                    value = item.payload.recoveryCodes.joinToString(" • "),
                    onCopy = { onCopySecret("Recovery Codes", item.payload.recoveryCodes.joinToString("\n")) },
                    testTag = "detail_copy_recovery_codes"
                )
            }

            // Notes
            if (item.payload.notes.isNotBlank()) {
                PlainFieldCard(
                    label = "Notes & Instructions",
                    value = item.payload.notes,
                    onCopy = { onCopySecret("Notes", item.payload.notes) },
                    testTag = "detail_copy_notes"
                )
            }

            // Timestamps Metadata
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Created: ${dateFormatter.format(Date(item.createdAt))}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                    Text(
                        text = "Last Updated: ${dateFormatter.format(Date(item.updatedAt))}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = SecurityRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Delete Vault Item?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${item.title}'? This action is permanent and cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteClick(item.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecurityRed),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete Item", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CharcoalSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Export PDF Dialog
    if (showExportPdfDialog && onExportPdf != null) {
        ItemExportPdfDialog(
            onDismiss = { showExportPdfDialog = false },
            onExportPdf = onExportPdf
        )
    }
}

@Composable
private fun ItemExportPdfDialog(
    onDismiss: () -> Unit,
    onExportPdf: (options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit
) {
    var includePasswords by remember { mutableStateOf(value = true) }
    var includeNotes by remember { mutableStateOf(value = true) }
    var includeTotp by remember { mutableStateOf(value = true) }
    var includeCrypto by remember { mutableStateOf(value = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Emergency Sheet (PDF)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Configure details to include in the printable PDF credential sheet:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Include Passwords & PINs")
                    Switch(
                        checked = includePasswords,
                        onCheckedChange = { includePasswords = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldPrimary)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Include Notes & URLs")
                    Switch(
                        checked = includeNotes,
                        onCheckedChange = { includeNotes = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldPrimary)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Include 2FA Seeds & Recovery Codes")
                    Switch(
                        checked = includeTotp,
                        onCheckedChange = { includeTotp = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldPrimary)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Include Crypto Secrets")
                    Switch(
                        checked = includeCrypto,
                        onCheckedChange = { includeCrypto = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldPrimary)
                    )
                }
            }
        },
        confirmButton = {
            Row {
                Button(
                    onClick = {
                        val opts = PdfExportManager.ExportOptions(
                            includePasswords = includePasswords,
                            includeNotes = includeNotes,
                            includeRecoveryCodes = includeTotp,
                            includeCryptoSecrets = includeCrypto
                        )
                        onExportPdf(opts, false)
                        onDismiss()
                    },
                    modifier = Modifier.testTag("export_pdf_save_button")
                ) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val opts = PdfExportManager.ExportOptions(
                            includePasswords = includePasswords,
                            includeNotes = includeNotes,
                            includeRecoveryCodes = includeTotp,
                            includeCryptoSecrets = includeCrypto
                        )
                        onExportPdf(opts, true)
                        onDismiss()
                    },
                    modifier = Modifier.testTag("export_pdf_share_button")
                ) {
                    Text("Share")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun PlainFieldCard(
    label: String,
    value: String,
    onCopy: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
            }

            IconButton(onClick = onCopy, modifier = Modifier.testTag(testTag)) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = EmeraldLight
                )
            }
        }
    }
}

@Composable
private fun SecretFieldCard(
    label: String,
    secretText: String,
    isRevealed: Boolean,
    onToggleReveal: () -> Unit,
    onCopy: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleReveal) {
                        Icon(
                            imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isRevealed) "Hide $label" else "Reveal $label",
                            tint = TextSecondary
                        )
                    }

                    IconButton(onClick = onCopy, modifier = Modifier.testTag(testTag)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy $label",
                            tint = EmeraldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isRevealed) secretText else "••••••••••••••••",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isRevealed) TextPrimary else TextMuted,
                    letterSpacing = if (isRevealed) 1.sp else 2.sp
                )
            )
        }
    }
}
