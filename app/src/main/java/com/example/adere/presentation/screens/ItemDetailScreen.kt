package com.example.adere.presentation.screens

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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.TOTPGenerator
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.presentation.components.SecurityWarningCard
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
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
    onLockClick: () -> Unit
) {
    var isPasswordRevealed by remember { mutableStateOf(false) }
    var isSeedRevealed by remember { mutableStateOf(false) }
    var isPrivateKeyRevealed by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

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
                    containerColor = CharcoalBg,
                    titleContentColor = TextPrimary
                )
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

            // Item Header Card with Real Brand Icon & Title
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.example.adere.presentation.components.VaultItemAvatar(
                        title = item.title,
                        category = item.category,
                        url = item.payload.url,
                        size = 52.dp,
                        iconSize = 28.dp,
                        cornerRadius = 12.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.category.title.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldLight
                            )
                        )
                    }
                }
            }

            // Category & Metadata Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.category.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Updated: ${dateFormatter.format(Date(item.updatedAt))}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
            }

            // Username / Account
            if (item.username.isNotBlank()) {
                DetailFieldCard(
                    label = if (item.category == VaultCategory.EMAIL) "Email" else "Username / Account",
                    value = item.username,
                    onCopy = { onCopySecret("Username", item.username) }
                )
            }

            // Password
            if (item.payload.password.isNotBlank()) {
                SecretDetailCard(
                    label = "Password",
                    secret = item.payload.password,
                    isRevealed = isPasswordRevealed,
                    onToggleReveal = { isPasswordRevealed = !isPasswordRevealed },
                    onCopy = { onCopySecret("Password", item.payload.password) }
                )
            }

            // PIN
            if (item.payload.pin.isNotBlank()) {
                SecretDetailCard(
                    label = "PIN",
                    secret = item.payload.pin,
                    isRevealed = isPasswordRevealed,
                    onToggleReveal = { isPasswordRevealed = !isPasswordRevealed },
                    onCopy = { onCopySecret("PIN", item.payload.pin) }
                )
            }

            // Live 2FA / TOTP Card
            if (item.payload.totpSecret.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldLight))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
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

                        if (totpResult != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = totpResult.formattedCode,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextPrimary,
                                        letterSpacing = 2.sp
                                    )
                                )
                                IconButton(onClick = { onCopySecret("2FA Code", totpResult.code) }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy 2FA code",
                                        tint = EmeraldLight
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { totpResult.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (totpResult.secondsRemaining > 5) EmeraldLight else SecurityRed,
                                trackColor = CharcoalSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Calculating OTP...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }

            // Website URL
            if (item.payload.url.isNotBlank()) {
                DetailFieldCard(
                    label = "Website URL",
                    value = item.payload.url,
                    onCopy = { onCopySecret("URL", item.payload.url) }
                )
            }

            // Crypto Vault Specifics
            if (item.category == VaultCategory.CRYPTO) {
                if (item.payload.cryptoNetwork.isNotBlank()) {
                    DetailFieldCard(
                        label = "Network / Blockchain",
                        value = item.payload.cryptoNetwork,
                        onCopy = null
                    )
                }

                if (item.payload.cryptoAddress.isNotBlank()) {
                    DetailFieldCard(
                        label = "Public Wallet Address",
                        value = item.payload.cryptoAddress,
                        onCopy = { onCopySecret("Crypto Address", item.payload.cryptoAddress) }
                    )
                }

                if (item.payload.cryptoSeedPhrase.isNotBlank()) {
                    SecurityWarningCard(
                        text = "Seed phrases provide full access to your funds. Never screenshot or share."
                    )
                    SecretDetailCard(
                        label = "Recovery Seed Phrase",
                        secret = item.payload.cryptoSeedPhrase,
                        isRevealed = isSeedRevealed,
                        onToggleReveal = { isSeedRevealed = !isSeedRevealed },
                        onCopy = { onCopySecret("Seed Phrase", item.payload.cryptoSeedPhrase) }
                    )
                }

                if (item.payload.cryptoPrivateKey.isNotBlank()) {
                    SecretDetailCard(
                        label = "Private Key",
                        secret = item.payload.cryptoPrivateKey,
                        isRevealed = isPrivateKeyRevealed,
                        onToggleReveal = { isPrivateKeyRevealed = !isPrivateKeyRevealed },
                        onCopy = { onCopySecret("Private Key", item.payload.cryptoPrivateKey) }
                    )
                }
            }

            // Wi-Fi Specifics
            if (item.category == VaultCategory.WIFI) {
                if (item.payload.wifiSsid.isNotBlank()) {
                    DetailFieldCard(
                        label = "Network Name (SSID)",
                        value = item.payload.wifiSsid,
                        onCopy = { onCopySecret("SSID", item.payload.wifiSsid) }
                    )
                }
                if (item.payload.wifiPassword.isNotBlank()) {
                    SecretDetailCard(
                        label = "Wi-Fi Password",
                        secret = item.payload.wifiPassword,
                        isRevealed = isPasswordRevealed,
                        onToggleReveal = { isPasswordRevealed = !isPasswordRevealed },
                        onCopy = { onCopySecret("Wi-Fi Password", item.payload.wifiPassword) }
                    )
                }
            }

            // Recovery Codes
            if (item.payload.recoveryCodes.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Backup Recovery Codes",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        for (code in item.payload.recoveryCodes) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isPasswordRevealed) code else "••••••••••••",
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                                IconButton(
                                    onClick = { onCopySecret("Recovery Code", code) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy code",
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Secure Notes
            if (item.payload.notes.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Encrypted Notes",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.payload.notes,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text(text = "Delete Item", color = TextPrimary) },
                text = {
                    Text(
                        text = "Are you sure you want to permanently delete '${item.title}' from your vault?",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = false
                            onDeleteClick(item.id)
                        }
                    ) {
                        Text("Delete", color = SecurityRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = CharcoalSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun DetailFieldCard(
    label: String,
    value: String,
    onCopy: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
            }
            if (onCopy != null) {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy $label",
                        tint = EmeraldLight
                    )
                }
            }
        }
    }
}

@Composable
private fun SecretDetailCard(
    label: String,
    secret: String,
    isRevealed: Boolean,
    onToggleReveal: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRevealed) secret else "••••••••••••••••",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = if (isRevealed) FontFamily.Monospace else FontFamily.Default,
                        color = TextPrimary
                    )
                )
            }
            IconButton(onClick = onToggleReveal) {
                Icon(
                    imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isRevealed) "Hide secret" else "Reveal secret",
                    tint = TextSecondary
                )
            }
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = EmeraldLight
                )
            }
        }
    }
}
