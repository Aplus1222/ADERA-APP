package com.example.adere.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.domain.model.VaultCategory
import com.example.adere.presentation.components.AdereTopBar
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SecurityBlue
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.res.painterResource
import com.example.adere.presentation.components.BrandIconHelper
import com.example.adere.presentation.components.BrandInfo

data class CategoryOption(
    val category: VaultCategory,
    val title: String,
    val subtitle: String,
    val examples: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryAddScreen(
    onSelectCategory: (VaultCategory) -> Unit,
    onSelectBrand: (BrandInfo) -> Unit,
    onLockClick: () -> Unit
) {
    var searchFilter by remember { mutableStateOf("") }

    val categories = remember {
        listOf(
            CategoryOption(
                category = VaultCategory.SOCIAL,
                title = "Social Media",
                subtitle = "Social accounts, logins & recovery info",
                examples = "Instagram, X / Twitter, TikTok, Facebook, LinkedIn",
                icon = Icons.Default.Share,
                iconColor = EmeraldLight,
                iconBgColor = EmeraldContainer
            ),
            CategoryOption(
                category = VaultCategory.EMAIL,
                title = "Email Accounts",
                subtitle = "Work, personal and alias mailboxes",
                examples = "Gmail, Outlook, ProtonMail, iCloud, Yahoo",
                icon = Icons.Default.Email,
                iconColor = Color(0xFF38BDF8),
                iconBgColor = Color(0xFF0C4A6E)
            ),
            CategoryOption(
                category = VaultCategory.BANKING,
                title = "Banking & Finance",
                subtitle = "Bank credentials, cards and payment IDs",
                examples = "Bank accounts, Credit cards, PayPal, Stripe",
                icon = Icons.Default.AccountBalance,
                iconColor = GoldAccent,
                iconBgColor = Color(0xFF452200)
            ),
            CategoryOption(
                category = VaultCategory.WEBSITE,
                title = "Web Services & Apps",
                subtitle = "E-commerce, subscriptions & SaaS portals",
                examples = "Amazon, Netflix, GitHub, Spotify, Apple ID",
                icon = Icons.Default.Public,
                iconColor = Color(0xFFA78BFA),
                iconBgColor = Color(0xFF2E1065)
            ),
            CategoryOption(
                category = VaultCategory.CRYPTO,
                title = "Crypto Vault",
                subtitle = "Cold storage, seed phrases & private keys",
                examples = "MetaMask, Ledger, Bitcoin, Ethereum, Solana",
                icon = Icons.Default.CurrencyBitcoin,
                iconColor = SecurityOrange,
                iconBgColor = Color(0xFF431407)
            ),
            CategoryOption(
                category = VaultCategory.WIFI,
                title = "Wi-Fi Networks",
                subtitle = "Router keys, office and guest network SSIDs",
                examples = "Home Wi-Fi, Office Router, Starlink, Hotspot",
                icon = Icons.Default.Wifi,
                iconColor = SecurityBlue,
                iconBgColor = Color(0xFF003258)
            ),
            CategoryOption(
                category = VaultCategory.NOTES,
                title = "Secure Notes",
                subtitle = "Encrypted private memos, PINs & secrets",
                examples = "Safe combinations, lock codes, secret diary",
                icon = Icons.AutoMirrored.Filled.Note,
                iconColor = Color(0xFFF472B6),
                iconBgColor = Color(0xFF500724)
            ),
            CategoryOption(
                category = VaultCategory.IDENTITY,
                title = "Personal Identity",
                subtitle = "Official IDs, passports & legal records",
                examples = "Passport numbers, National ID, Driver's License",
                icon = Icons.Default.Badge,
                iconColor = Color(0xFF2DD4BF),
                iconBgColor = Color(0xFF042F2E)
            ),
            CategoryOption(
                category = VaultCategory.TOTP_2FA,
                title = "2FA Authenticator",
                subtitle = "Time-based one-time password secret keys",
                examples = "Google Auth seeds, Authy, Microsoft 2FA",
                icon = Icons.Default.QrCode,
                iconColor = Color(0xFFFBBF24),
                iconBgColor = Color(0xFF451A03)
            ),
            CategoryOption(
                category = VaultCategory.RECOVERY,
                title = "Recovery Codes",
                subtitle = "Single-use emergency codes & backup keys",
                examples = "Google backup codes, GitHub recovery keys",
                icon = Icons.Default.LockReset,
                iconColor = Color(0xFFF87171),
                iconBgColor = Color(0xFF450A0A)
            ),
            CategoryOption(
                category = VaultCategory.OTHER,
                title = "Custom Secret",
                subtitle = "Other sensitive passwords and credentials",
                examples = "Server SSH keys, API tokens, database logins",
                icon = Icons.Default.Key,
                iconColor = Color(0xFF94A3B8),
                iconBgColor = Color(0xFF1E293B)
            )
        )
    }

    val filteredCategories = remember(searchFilter, categories) {
        if (searchFilter.isBlank()) {
            categories
        } else {
            categories.filter {
                it.title.contains(searchFilter, ignoreCase = true) ||
                it.subtitle.contains(searchFilter, ignoreCase = true) ||
                it.examples.contains(searchFilter, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            AdereTopBar(
                title = "Add to Vault",
                subtitle = "Select a credential category",
                onLockClick = onLockClick
            )
        },
        containerColor = CharcoalBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Description Card
            item {
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
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Categorized Vault Storage",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Choose a category below to generate passwords and store encrypted credentials.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text("Search category or service (e.g. Wi-Fi, Bank)...", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CharcoalSurface,
                        unfocusedContainerColor = CharcoalSurface,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = CharcoalBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_category_search_input")
                )
            }

            // Popular Social Media Apps Section with Real Icons
            if (searchFilter.isBlank()) {
                item {
                    Text(
                        text = "POPULAR SOCIAL & ONLINE APPS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "One-Tap Add Common Accounts",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Select an app to open a pre-filled credential card with authentic brand logo",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (brand in BrandIconHelper.POPULAR_SOCIAL_BRANDS) {
                                    PopularAppChip(
                                        brand = brand,
                                        onClick = { onSelectBrand(brand) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section Label
            item {
                Text(
                    text = "ALL CATEGORIES (${filteredCategories.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                )
            }

            // List of Category Cards
            items(filteredCategories, key = { it.category.name }) { option ->
                CategoryCardRow(
                    option = option,
                    onClick = { onSelectCategory(option.category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCardRow(
    option: CategoryOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("category_card_${option.category.name.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(option.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    tint = option.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = option.examples,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Select",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PopularAppChip(
    brand: BrandInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("popular_app_${brand.id}"),
        colors = CardDefaults.cardColors(containerColor = CharcoalBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = brand.iconRes),
                contentDescription = brand.name,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = brand.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
        }
    }
}

