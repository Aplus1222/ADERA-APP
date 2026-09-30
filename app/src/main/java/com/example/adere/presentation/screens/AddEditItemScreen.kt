package com.example.adere.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.core.crypto.TOTPGenerator
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import com.example.adere.presentation.components.CategoryChip
import com.example.adere.presentation.components.PasswordStrengthBar
import com.example.adere.presentation.components.SecurityWarningCard
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    initialItem: VaultItem?,
    initialCategory: VaultCategory,
    onBackClick: () -> Unit,
    onSaveItem: (VaultItem) -> Unit
) {
    val isEditMode = initialItem != null

    var category by remember { mutableStateOf(initialItem?.category ?: initialCategory) }
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var username by remember { mutableStateOf(initialItem?.username ?: "") }
    var password by remember { mutableStateOf(initialItem?.payload?.password ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf(initialItem?.payload?.pin ?: "") }
    var url by remember { mutableStateOf(initialItem?.payload?.url ?: "") }
    var notes by remember { mutableStateOf(initialItem?.payload?.notes ?: "") }
    var totpSecret by remember { mutableStateOf(initialItem?.payload?.totpSecret ?: "") }

    // Crypto fields
    var cryptoNetwork by remember { mutableStateOf(initialItem?.payload?.cryptoNetwork ?: "") }
    var cryptoAddress by remember { mutableStateOf(initialItem?.payload?.cryptoAddress ?: "") }
    var cryptoSeedPhrase by remember { mutableStateOf(initialItem?.payload?.cryptoSeedPhrase ?: "") }
    var cryptoPrivateKey by remember { mutableStateOf(initialItem?.payload?.cryptoPrivateKey ?: "") }

    // Wi-Fi fields
    var wifiSsid by remember { mutableStateOf(initialItem?.payload?.wifiSsid ?: "") }
    var wifiPassword by remember { mutableStateOf(initialItem?.payload?.wifiPassword ?: "") }

    // Recovery codes
    val recoveryCodes = remember {
        mutableStateListOf<String>().apply {
            addAll(initialItem?.payload?.recoveryCodes ?: emptyList())
        }
    }
    var newRecoveryCode by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Real-time password evaluation
    val entropy = remember(password) { PasswordGenerator.calculateEntropy(password) }
    val strength = remember(password, entropy) { PasswordGenerator.evaluateStrength(entropy, password.length) }

    // Real-time TOTP preview test
    val liveTotpTest = remember(totpSecret) {
        if (totpSecret.isNotBlank()) {
            TOTPGenerator.generateCurrentTotp(totpSecret)
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Item" else "New Vault Item") },
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
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter an item title."
                                return@Button
                            }
                            val updatedPayload = VaultItemPayload(
                                password = password,
                                pin = pin,
                                url = url,
                                notes = notes,
                                totpSecret = totpSecret.trim(),
                                cryptoNetwork = cryptoNetwork,
                                cryptoAddress = cryptoAddress,
                                cryptoSeedPhrase = cryptoSeedPhrase,
                                cryptoPrivateKey = cryptoPrivateKey,
                                wifiSsid = wifiSsid,
                                wifiPassword = wifiPassword,
                                recoveryCodes = recoveryCodes.toList()
                            )
                            val item = VaultItem(
                                id = initialItem?.id ?: UUID.randomUUID().toString(),
                                category = category,
                                title = title.trim(),
                                username = username.trim(),
                                payload = updatedPayload,
                                isFavorite = initialItem?.isFavorite ?: false,
                                createdAt = initialItem?.createdAt ?: System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis(),
                                passwordLastChanged = if (initialItem?.payload?.password != password) {
                                    System.currentTimeMillis()
                                } else {
                                    initialItem.passwordLastChanged
                                }
                            )
                            onSaveItem(item)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color(0xFF003824)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_vault_item_button")
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
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

            // Category Selector
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val availableCategories = listOf(
                    VaultCategory.SOCIAL,
                    VaultCategory.EMAIL,
                    VaultCategory.BANKING,
                    VaultCategory.CRYPTO,
                    VaultCategory.WEBSITE,
                    VaultCategory.WIFI,
                    VaultCategory.IDENTITY,
                    VaultCategory.NOTES,
                    VaultCategory.TOTP_2FA,
                    VaultCategory.RECOVERY,
                    VaultCategory.OTHER
                )
                items(availableCategories) { cat ->
                    CategoryChip(
                        category = cat,
                        isSelected = category == cat,
                        onClick = { category = cat }
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = SecurityRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Quick Popular Brands Bar
            if (!isEditMode) {
                Text(
                    text = "Quick Choose Common App",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.SemiBold)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(com.example.adere.presentation.components.BrandIconHelper.POPULAR_SOCIAL_BRANDS) { brand ->
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    title = brand.name
                                    url = brand.defaultUrl
                                    category = brand.category
                                    errorMessage = null
                                }
                                .testTag("quick_brand_${brand.id}"),
                            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = brand.iconRes),
                                    contentDescription = brand.name,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = brand.name,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Title with dynamic brand icon avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                com.example.adere.presentation.components.VaultItemAvatar(
                    title = title,
                    category = category,
                    url = url,
                    size = 50.dp,
                    iconSize = 26.dp,
                    cornerRadius = 12.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Title (e.g. Instagram, Facebook, Gmail)") },
                    singleLine = true,
                    colors = defaultTextFieldColors(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("item_title_input")
                )
            }

            // Category Specific Hints & Fields
            when (category) {
                VaultCategory.CRYPTO -> {
                    SecurityWarningCard(
                        text = "Crypto Vault: Encrypted with your vault key. Never share your seed phrase or private key with anyone."
                    )
                    OutlinedTextField(
                        value = cryptoNetwork,
                        onValueChange = { cryptoNetwork = it },
                        label = { Text("Blockchain / Network (e.g. Bitcoin, Solana, Ethereum)") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cryptoAddress,
                        onValueChange = { cryptoAddress = it },
                        label = { Text("Public Wallet Address") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cryptoSeedPhrase,
                        onValueChange = { cryptoSeedPhrase = it },
                        label = { Text("Seed Phrase (12/24 words)") },
                        colors = defaultTextFieldColors(),
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cryptoPrivateKey,
                        onValueChange = { cryptoPrivateKey = it },
                        label = { Text("Private Key") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                VaultCategory.WIFI -> {
                    OutlinedTextField(
                        value = wifiSsid,
                        onValueChange = { wifiSsid = it },
                        label = { Text("Wi-Fi SSID (Network Name)") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = wifiPassword,
                        onValueChange = { wifiPassword = it },
                        label = { Text("Wi-Fi Password") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                VaultCategory.BANKING -> {
                    SecurityWarningCard(
                        text = "Never store sensitive banking PINs unless you understand the physical security of your device."
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Customer ID / Account Login") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text("PIN / Security Code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    // Standard Login Credential Fields
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(if (category == VaultCategory.EMAIL) "Email Address" else "Username / Email") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_username_input")
                    )

                    // Password Field with Generator Action
                    Column {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val generated = PasswordGenerator.generate(PasswordGenerator.GeneratorOptions(length = 20))
                                            password = generated.password
                                            passwordVisible = true
                                        },
                                        modifier = Modifier.testTag("generate_inline_password_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Generate random password",
                                            tint = EmeraldLight
                                        )
                                    }
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            },
                            colors = defaultTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_password_input")
                        )
                        if (password.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            PasswordStrengthBar(strength = strength, entropyBits = entropy)
                        }
                    }

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("Website URL (e.g. https://instagram.com)") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2FA / TOTP Secret Field
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Two-Factor Authentication (TOTP 2FA)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = totpSecret,
                        onValueChange = { totpSecret = it },
                        label = { Text("2FA Secret Key (Base32, e.g. JBSWY3DPEHPK3PXP)") },
                        singleLine = true,
                        colors = defaultTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_totp_input")
                    )
                    if (liveTotpTest != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live preview: ",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = liveTotpTest.formattedCode,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldLight
                                )
                            )
                            Text(
                                text = " (${liveTotpTest.secondsRemaining}s)",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    } else if (totpSecret.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Invalid Base32 2FA secret",
                            style = MaterialTheme.typography.labelSmall.copy(color = SecurityRed)
                        )
                    }
                }
            }

            // Recovery Codes Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Recovery Codes",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newRecoveryCode,
                            onValueChange = { newRecoveryCode = it },
                            placeholder = { Text("e.g. 1a2b-3c4d-5e6f", color = TextMuted) },
                            singleLine = true,
                            colors = defaultTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (newRecoveryCode.isNotBlank()) {
                                    recoveryCodes.add(newRecoveryCode.trim())
                                    newRecoveryCode = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add recovery code",
                                tint = EmeraldLight
                            )
                        }
                    }

                    if (recoveryCodes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        for (code in recoveryCodes) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = code,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                                )
                                IconButton(
                                    onClick = { recoveryCodes.remove(code) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove code",
                                        tint = SecurityRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Secure Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Secure Notes (Encrypted)") },
                minLines = 3,
                colors = defaultTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("item_notes_input")
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EmeraldPrimary,
    unfocusedBorderColor = CharcoalBorder,
    focusedContainerColor = CharcoalSurface,
    unfocusedContainerColor = CharcoalSurface,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)
