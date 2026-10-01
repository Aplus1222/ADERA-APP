package com.example.adere.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.CategoryAppsYellow
import com.example.ui.theme.CategoryCardTeal
import com.example.ui.theme.CleanBg
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.CleanSurface
import com.example.ui.theme.CleanSurfaceVariant
import com.example.ui.theme.HealthCompromisedTeal
import com.example.ui.theme.HealthSafeBlue
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TotalSecurityPrimary
import com.example.ui.theme.TotalSecurityPrimaryContainer
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var isFavorite by remember { mutableStateOf(initialItem?.isFavorite ?: false) }

    // Banking fields
    var cardNumber by remember { mutableStateOf(initialItem?.payload?.cardNumber ?: "") }
    var cardExpiry by remember { mutableStateOf(initialItem?.payload?.cardExpiry ?: "") }
    var cardCvv by remember { mutableStateOf(initialItem?.payload?.cardCvv ?: "") }
    var cardHolder by remember { mutableStateOf(initialItem?.payload?.cardHolder ?: "") }
    var bankName by remember { mutableStateOf(initialItem?.payload?.bankName ?: "") }

    // Identity fields
    var docType by remember { mutableStateOf(initialItem?.payload?.docType ?: "Passport") }
    var docNumber by remember { mutableStateOf(initialItem?.payload?.docNumber ?: "") }

    // Crypto fields
    var cryptoNetwork by remember { mutableStateOf(initialItem?.payload?.cryptoNetwork ?: "Ethereum") }
    var cryptoAddress by remember { mutableStateOf(initialItem?.payload?.cryptoAddress ?: "") }
    var cryptoSeedPhrase by remember { mutableStateOf(initialItem?.payload?.cryptoSeedPhrase ?: "") }
    var cryptoPrivateKey by remember { mutableStateOf(initialItem?.payload?.cryptoPrivateKey ?: "") }
    var seedPhraseVisible by remember { mutableStateOf(false) }

    // Wi-Fi fields
    var wifiSsid by remember { mutableStateOf(initialItem?.payload?.wifiSsid ?: "") }
    var wifiPassword by remember { mutableStateOf(initialItem?.payload?.wifiPassword ?: "") }
    var wifiSecurityType by remember { mutableStateOf(initialItem?.payload?.wifiSecurityType ?: "WPA2/WPA3") }

    // Recovery codes list
    val recoveryCodes = remember {
        mutableStateListOf<String>().apply {
            addAll(initialItem?.payload?.recoveryCodes ?: emptyList())
        }
    }
    var newRecoveryCode by remember { mutableStateOf("") }

    // Custom Fields
    val customFields = remember {
        mutableStateMapOf<String, String>().apply {
            putAll(initialItem?.payload?.customFields ?: emptyMap())
        }
    }
    var showAddCustomFieldDialog by remember { mutableStateOf(false) }
    var customFieldKeyInput by remember { mutableStateOf("") }
    var customFieldValueInput by remember { mutableStateOf("") }

    // In-form Password Generator Sheet/Dialog
    var showGeneratorDialog by remember { mutableStateOf(false) }
    var genLength by remember { mutableFloatStateOf(16f) }
    var genUseUpper by remember { mutableStateOf(true) }
    var genUseLower by remember { mutableStateOf(true) }
    var genUseDigits by remember { mutableStateOf(true) }
    var genUseSymbols by remember { mutableStateOf(true) }
    var generatedPreview by remember { mutableStateOf("") }

    fun refreshGeneratedPassword() {
        val options = PasswordGenerator.GeneratorOptions(
            length = genLength.toInt(),
            includeUppercase = genUseUpper,
            includeLowercase = genUseLower,
            includeDigits = genUseDigits,
            includeSymbols = genUseSymbols
        )
        generatedPreview = PasswordGenerator.generate(options).password
    }

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
                title = {
                    Text(
                        text = if (isEditMode) "Edit Item" else "New Vault Item",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDarkPrimary
                        )
                    }
                },
                actions = {
                    // Favorite Toggle Action
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (isFavorite) CategoryAppsYellow else TextDarkMuted
                        )
                    }

                    // Save Button
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
                                wifiSecurityType = wifiSecurityType,
                                cardNumber = cardNumber,
                                cardExpiry = cardExpiry,
                                cardCvv = cardCvv,
                                cardHolder = cardHolder,
                                bankName = bankName,
                                docType = docType,
                                docNumber = docNumber,
                                recoveryCodes = recoveryCodes.toList(),
                                customFields = customFields.toMap()
                            )
                            val item = VaultItem(
                                id = initialItem?.id ?: UUID.randomUUID().toString(),
                                category = category,
                                title = title.trim(),
                                username = username.trim(),
                                payload = updatedPayload,
                                isFavorite = isFavorite,
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
                            containerColor = TotalSecurityPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_vault_item_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CleanBg,
                    titleContentColor = TextDarkPrimary
                )
            )
        },
        containerColor = CleanBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // Category Selector Chips
            Text(
                text = "Item Category",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.Bold
                )
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
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = SecurityRed,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Title & Visual Avatar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.adere.presentation.components.VaultItemAvatar(
                            title = title,
                            category = category,
                            url = url,
                            size = 52.dp,
                            iconSize = 28.dp,
                            cornerRadius = 14.dp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                errorMessage = null
                            },
                            label = { Text("Title (e.g. Google, GitHub, Chase)") },
                            placeholder = { Text("Enter a recognizable label") },
                            singleLine = true,
                            colors = cleanTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("item_title_input")
                        )
                    }
                }
            }

            // Category Specific Rich Forms
            when (category) {
                VaultCategory.BANKING -> {
                    // Credit Card / Debit Card & Banking Form
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE6FAF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = CategoryCardTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Payment Card & Bank Details",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkPrimary
                                        )
                                    )
                                    Text(
                                        text = "Hardware encrypted with zero network transmission",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextDarkSecondary)
                                    )
                                }
                            }

                            // Cardholder Name
                            OutlinedTextField(
                                value = cardHolder,
                                onValueChange = { cardHolder = it },
                                label = { Text("Cardholder Name") },
                                placeholder = { Text("e.g. ALEX MORGAN") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Card Number
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { raw ->
                                    val digitsOnly = raw.filter { it.isDigit() }.take(19)
                                    cardNumber = digitsOnly.chunked(4).joinToString(" ")
                                },
                                label = { Text("Card Number") },
                                placeholder = { Text("•••• •••• •••• ••••") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Expiry & CVV
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { raw ->
                                        val digits = raw.filter { it.isDigit() }.take(4)
                                        cardExpiry = if (digits.length > 2) "${digits.take(2)}/${digits.drop(2)}" else digits
                                    },
                                    label = { Text("Expiry (MM/YY)") },
                                    placeholder = { Text("12/28") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = cleanTextFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { cardCvv = it.filter { ch -> ch.isDigit() }.take(4) },
                                    label = { Text("CVV / CVC") },
                                    placeholder = { Text("•••") },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    colors = cleanTextFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Card PIN
                            OutlinedTextField(
                                value = pin,
                                onValueChange = { pin = it.filter { ch -> ch.isDigit() }.take(6) },
                                label = { Text("Card ATM PIN") },
                                placeholder = { Text("4-digit code") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Bank Name
                            OutlinedTextField(
                                value = bankName,
                                onValueChange = { bankName = it },
                                label = { Text("Issuing Bank / Portal Name") },
                                placeholder = { Text("e.g. Chase, Revolut, HSBC") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                VaultCategory.IDENTITY -> {
                    // Identity & Official Documents
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Document Type",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextDarkSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            val docTypes = listOf("Passport", "Driver's License", "National ID", "Social Security")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (type in docTypes) {
                                    val isSelected = docType == type
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { docType = type },
                                        label = { Text(type, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = TotalSecurityPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = docNumber,
                                onValueChange = { docNumber = it },
                                label = { Text("Document / Identification Number") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Full Legal Name on Document") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                VaultCategory.CRYPTO -> {
                    // Crypto Wallets & Mnemonic Seeds
                    SecurityWarningCard(
                        text = "Crypto Vault: Encrypted with hardware AES-256-GCM. Never share your seed phrase or private key with anyone."
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Blockchain Network",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextDarkSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            val networks = listOf("Bitcoin", "Ethereum", "Solana", "BNB Chain", "Polygon", "Arbitrum")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (net in networks) {
                                    val isSelected = cryptoNetwork.equals(net, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { cryptoNetwork = net },
                                        label = { Text(net, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFD97706),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = cryptoAddress,
                                onValueChange = { cryptoAddress = it },
                                label = { Text("Public Wallet Address (0x... / bc1...)") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            val seedWords = remember(cryptoSeedPhrase) {
                                cryptoSeedPhrase.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recovery Seed Phrase (${seedWords.size} words)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (seedWords.size == 12 || seedWords.size == 24) Color(0xFF10B981) else TextDarkSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                TextButton(onClick = { seedPhraseVisible = !seedPhraseVisible }) {
                                    Text(
                                        text = if (seedPhraseVisible) "Mask" else "Reveal",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = cryptoSeedPhrase,
                                onValueChange = { cryptoSeedPhrase = it },
                                placeholder = { Text("e.g. anchor beacon citadel delta ember falcon...", color = TextDarkMuted) },
                                visualTransformation = if (seedPhraseVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = cryptoPrivateKey,
                                onValueChange = { cryptoPrivateKey = it },
                                label = { Text("Private Key (Irreversible control)") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                VaultCategory.WIFI -> {
                    // Wi-Fi Configuration
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = wifiSsid,
                                onValueChange = { wifiSsid = it },
                                label = { Text("Wi-Fi SSID (Network Name)") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = wifiPassword,
                                onValueChange = { wifiPassword = it },
                                label = { Text("Wi-Fi Password / Pre-Shared Key") },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = TextDarkSecondary
                                        )
                                    }
                                },
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Security Protocol",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextDarkSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            val securityTypes = listOf("WPA2/WPA3", "WPA2-Personal", "WPA3-Personal", "Open")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (sec in securityTypes) {
                                    val isSelected = wifiSecurityType == sec
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { wifiSecurityType = sec },
                                        label = { Text(sec, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = TotalSecurityPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Standard Login Credential Fields (Social, Email, Website, etc.)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Quick Domain helper chips for Email
                            if (category == VaultCategory.EMAIL) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val domains = listOf("@gmail.com", "@outlook.com", "@icloud.com", "@proton.me")
                                    for (domain in domains) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = CleanSurfaceVariant,
                                            modifier = Modifier.clickable {
                                                if (!username.contains("@")) {
                                                    username = username.trim() + domain
                                                } else {
                                                    val prefix = username.substringBefore("@")
                                                    username = prefix + domain
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = domain,
                                                color = TotalSecurityPrimary,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Username / Email
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text(if (category == VaultCategory.EMAIL) "Email Address" else "Username / Email") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
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
                                                    refreshGeneratedPassword()
                                                    showGeneratorDialog = true
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = "Generate Strong Password",
                                                    tint = TotalSecurityPrimary
                                                )
                                            }
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                                    contentDescription = null,
                                                    tint = TextDarkSecondary
                                                )
                                            }
                                        }
                                    },
                                    colors = cleanTextFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("item_password_input")
                                )

                                if (password.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    PasswordStrengthBar(strength = strength, entropyBits = entropy)
                                }
                            }

                            // Website URL
                            OutlinedTextField(
                                value = url,
                                onValueChange = { url = it },
                                label = { Text("Website URL (e.g. https://instagram.com)") },
                                singleLine = true,
                                colors = cleanTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // 2FA / TOTP Secret Field Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Two-Factor Authenticator (TOTP 2FA)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        )
                    )
                    Text(
                        text = "Generates offline rotating 6-digit codes",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = totpSecret,
                        onValueChange = { totpSecret = it },
                        label = { Text("2FA Secret Key (Base32, e.g. JBSWY3DPEHPK3PXP)") },
                        placeholder = { Text("Paste authenticator secret key") },
                        singleLine = true,
                        colors = cleanTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_totp_input")
                    )

                    if (liveTotpTest != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TotalSecurityPrimaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Current Code: ",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkSecondary)
                                    )
                                    Text(
                                        text = liveTotpTest.formattedCode,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TotalSecurityPrimary,
                                            letterSpacing = 2.sp
                                        )
                                    )
                                }
                                Text(
                                    text = "${liveTotpTest.secondsRemaining}s remaining",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextDarkMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
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

            // Custom Secure Fields Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Custom Secure Fields",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            )
                            Text(
                                text = "Add secret questions, client IDs, or API keys",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                            )
                        }

                        IconButton(onClick = { showAddCustomFieldDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Custom Field",
                                tint = TotalSecurityPrimary
                            )
                        }
                    }

                    if (customFields.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        customFields.forEach { (key, value) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CleanSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextDarkSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = value,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextDarkPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                    IconButton(
                                        onClick = { customFields.remove(key) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove field",
                                            tint = SecurityRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recovery Codes Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Recovery Backup Codes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
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
                            placeholder = { Text("e.g. 1a2b-3c4d-5e6f", color = TextDarkMuted) },
                            singleLine = true,
                            colors = cleanTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
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
                                tint = TotalSecurityPrimary
                            )
                        }
                    }

                    if (recoveryCodes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        for (code in recoveryCodes) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = code,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextDarkPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Encrypted Notes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("Enter private notes, recovery steps, or instructions...", color = TextDarkMuted) },
                        minLines = 3,
                        colors = cleanTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_notes_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Generator Dialog
    if (showGeneratorDialog) {
        AlertDialog(
            onDismissRequest = { showGeneratorDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(TotalSecurityPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TotalSecurityPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Strong Password Generator",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Generated Preview Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CleanSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = generatedPreview,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDarkPrimary,
                                    letterSpacing = 1.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Length Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Length: ${genLength.toInt()} characters",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                        )
                    }

                    Slider(
                        value = genLength,
                        onValueChange = {
                            genLength = it
                            refreshGeneratedPassword()
                        },
                        valueRange = 8f..40f,
                        steps = 31,
                        colors = SliderDefaults.colors(
                            thumbColor = TotalSecurityPrimary,
                            activeTrackColor = TotalSecurityPrimary
                        )
                    )

                    // Options Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = genUseUpper,
                            onClick = {
                                genUseUpper = !genUseUpper
                                refreshGeneratedPassword()
                            },
                            label = { Text("A-Z", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TotalSecurityPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = genUseLower,
                            onClick = {
                                genUseLower = !genUseLower
                                refreshGeneratedPassword()
                            },
                            label = { Text("a-z", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TotalSecurityPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = genUseDigits,
                            onClick = {
                                genUseDigits = !genUseDigits
                                refreshGeneratedPassword()
                            },
                            label = { Text("0-9", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TotalSecurityPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = genUseSymbols,
                            onClick = {
                                genUseSymbols = !genUseSymbols
                                refreshGeneratedPassword()
                            },
                            label = { Text("!@#$", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TotalSecurityPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    TextButton(
                        onClick = { refreshGeneratedPassword() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = TotalSecurityPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reroll Password",
                            color = TotalSecurityPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        password = generatedPreview
                        showGeneratorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TotalSecurityPrimary)
                ) {
                    Text("Apply to Password", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGeneratorDialog = false }) {
                    Text("Cancel", color = TextDarkSecondary)
                }
            },
            containerColor = CleanSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Add Custom Field Dialog
    if (showAddCustomFieldDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomFieldDialog = false },
            title = {
                Text(
                    text = "Add Custom Field",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = customFieldKeyInput,
                        onValueChange = { customFieldKeyInput = it },
                        label = { Text("Field Label") },
                        placeholder = { Text("e.g. Security Answer, Client ID") },
                        singleLine = true,
                        colors = cleanTextFieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customFieldValueInput,
                        onValueChange = { customFieldValueInput = it },
                        label = { Text("Field Value") },
                        singleLine = true,
                        colors = cleanTextFieldColors(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customFieldKeyInput.isNotBlank()) {
                            customFields[customFieldKeyInput.trim()] = customFieldValueInput.trim()
                            customFieldKeyInput = ""
                            customFieldValueInput = ""
                            showAddCustomFieldDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TotalSecurityPrimary)
                ) {
                    Text("Add Field", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomFieldDialog = false }) {
                    Text("Cancel", color = TextDarkSecondary)
                }
            },
            containerColor = CleanSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun cleanTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TotalSecurityPrimary,
    unfocusedBorderColor = Color.Transparent,
    focusedContainerColor = CleanSurfaceVariant,
    unfocusedContainerColor = CleanSurfaceVariant,
    focusedTextColor = TextDarkPrimary,
    unfocusedTextColor = TextDarkPrimary
)
