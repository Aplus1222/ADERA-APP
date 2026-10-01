package com.example.adere.presentation.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.adere.domain.model.VaultCategory
import com.example.adere.presentation.components.AdereTopBar
import com.example.adere.presentation.components.BrandIconHelper
import com.example.adere.presentation.components.BrandInfo
import com.example.ui.theme.CategoryAppsYellow
import com.example.ui.theme.CategoryCardTeal
import com.example.ui.theme.CategorySocialBlue
import com.example.ui.theme.CleanBg
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.CleanSurface
import com.example.ui.theme.CleanSurfaceVariant
import com.example.ui.theme.HealthCompromisedTeal
import com.example.ui.theme.HealthRefusedYellow
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.HealthSafeBlue
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TotalSecurityPrimary
import com.example.ui.theme.TotalSecurityPrimaryContainer

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
    var selectedTab by remember { mutableStateOf("All") }
    val tabs = remember { listOf("All", "Logins", "Crypto", "Banking", "Identity", "Wi-Fi", "Notes & 2FA") }

    val categories = remember {
        listOf(
            CategoryOption(
                category = VaultCategory.SOCIAL,
                title = "Social Media",
                subtitle = "Facebook, Instagram, X, TikTok, Telegram",
                examples = "Social logins, usernames, recovery codes",
                icon = Icons.Default.Share,
                iconColor = TotalSecurityPrimary,
                iconBgColor = TotalSecurityPrimaryContainer
            ),
            CategoryOption(
                category = VaultCategory.CRYPTO,
                title = "Crypto Vault",
                subtitle = "MetaMask, Bitcoin, Ethereum, Seed Phrases",
                examples = "12/24-word recovery phrases, private keys, wallet addresses",
                icon = Icons.Default.CurrencyBitcoin,
                iconColor = Color(0xFFF7931A),
                iconBgColor = Color(0xFFFEF3C7)
            ),
            CategoryOption(
                category = VaultCategory.EMAIL,
                title = "Email Accounts",
                subtitle = "Gmail, Outlook, ProtonMail, iCloud",
                examples = "Mailboxes, app passwords, backup recovery codes",
                icon = Icons.Default.Email,
                iconColor = Color(0xFF4285F4),
                iconBgColor = Color(0xFFE8F0FE)
            ),
            CategoryOption(
                category = VaultCategory.BANKING,
                title = "Banking & Cards",
                subtitle = "Credit cards, bank logins, security PINs",
                examples = "Visa, Mastercard, Bank portal logins, IBAN",
                icon = Icons.Default.AccountBalance,
                iconColor = CategoryCardTeal,
                iconBgColor = Color(0xFFE6FAF5)
            ),
            CategoryOption(
                category = VaultCategory.WEBSITE,
                title = "Web Services & Apps",
                subtitle = "Figma, Spotify, Netflix, GitHub, Apple ID",
                examples = "SaaS accounts, e-commerce, cloud subscriptions",
                icon = Icons.Default.Public,
                iconColor = Color(0xFFA855F7),
                iconBgColor = Color(0xFFF3E8FF)
            ),
            CategoryOption(
                category = VaultCategory.WIFI,
                title = "Wi-Fi Networks",
                subtitle = "Router keys, home & office Wi-Fi",
                examples = "Network passwords, WPA3 credentials, guest access",
                icon = Icons.Default.Wifi,
                iconColor = Color(0xFF0EA5E9),
                iconBgColor = Color(0xFFE0F2FE)
            ),
            CategoryOption(
                category = VaultCategory.NOTES,
                title = "Secure Notes",
                subtitle = "Encrypted private memos & secrets",
                examples = "Safe combinations, confidential diaries, codes",
                icon = Icons.AutoMirrored.Filled.Note,
                iconColor = Color(0xFFEC4899),
                iconBgColor = Color(0xFFFCE7F3)
            ),
            CategoryOption(
                category = VaultCategory.IDENTITY,
                title = "Personal Identity",
                subtitle = "Passports, IDs & official documents",
                examples = "Driver's license, SSN, National ID numbers",
                icon = Icons.Default.Badge,
                iconColor = Color(0xFF14B8A6),
                iconBgColor = Color(0xFFCCFBF1)
            ),
            CategoryOption(
                category = VaultCategory.TOTP_2FA,
                title = "2FA Authenticator",
                subtitle = "Time-based OTP security keys",
                examples = "Google Authenticator seeds, backup codes",
                icon = Icons.Default.QrCode,
                iconColor = CategoryAppsYellow,
                iconBgColor = Color(0xFFFEF3C7)
            ),
            CategoryOption(
                category = VaultCategory.RECOVERY,
                title = "Recovery Codes",
                subtitle = "Emergency backup codes for accounts",
                examples = "One-time account recovery backup kits",
                icon = Icons.Default.LockReset,
                iconColor = HealthRiskRed,
                iconBgColor = Color(0xFFFEE2E2)
            ),
            CategoryOption(
                category = VaultCategory.OTHER,
                title = "Other Credentials",
                subtitle = "Custom encrypted fields & secrets",
                examples = "Server SSH keys, database logins, API keys",
                icon = Icons.Default.Key,
                iconColor = Color(0xFF64748B),
                iconBgColor = Color(0xFFF1F5F9)
            )
        )
    }

    val filteredCategories = remember(searchFilter, selectedTab, categories) {
        val byTab = when (selectedTab) {
            "Logins" -> categories.filter { it.category in listOf(VaultCategory.SOCIAL, VaultCategory.EMAIL, VaultCategory.WEBSITE) }
            "Crypto" -> categories.filter { it.category == VaultCategory.CRYPTO }
            "Banking" -> categories.filter { it.category == VaultCategory.BANKING }
            "Identity" -> categories.filter { it.category == VaultCategory.IDENTITY }
            "Wi-Fi" -> categories.filter { it.category == VaultCategory.WIFI }
            "Notes & 2FA" -> categories.filter { it.category in listOf(VaultCategory.NOTES, VaultCategory.TOTP_2FA, VaultCategory.RECOVERY, VaultCategory.OTHER) }
            else -> categories
        }

        if (searchFilter.isBlank()) {
            byTab
        } else {
            byTab.filter {
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
                subtitle = "Select credential category or app",
                onLockClick = onLockClick
            )
        },
        containerColor = CleanBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text("Search apps, crypto, social, or categories...", color = TextDarkMuted, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextDarkMuted)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CleanSurface,
                        unfocusedContainerColor = CleanSurface,
                        focusedBorderColor = TotalSecurityPrimary,
                        unfocusedBorderColor = CleanBorder,
                        focusedTextColor = TextDarkPrimary,
                        unfocusedTextColor = TextDarkPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_category_search_input")
                )
            }

            // Quick Category Filter Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs) { tab ->
                        val isSelected = selectedTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            label = {
                                Text(
                                    text = tab,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TotalSecurityPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (searchFilter.isBlank()) {
                // Section 1: CRYPTO WALLETS & SEED PHRASES
                item {
                    Text(
                        text = "CRYPTO WALLET & SEED PHRASES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Cold Storage & Web3 Accounts",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            )
                            Text(
                                text = "Secure 12/24-word recovery phrases, private keys and wallet addresses",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (brand in BrandIconHelper.POPULAR_CRYPTO_BRANDS) {
                                    BrandQuickChip(
                                        brand = brand,
                                        onClick = { onSelectBrand(brand) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 2: GMAIL & EMAIL ACCOUNTS
                item {
                    Text(
                        text = "GMAIL & EMAIL ACCOUNTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Google & Mailbox Services",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (brand in BrandIconHelper.POPULAR_EMAIL_BRANDS) {
                                    BrandQuickChip(
                                        brand = brand,
                                        onClick = { onSelectBrand(brand) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: POPULAR SOCIAL MEDIA APPS
                item {
                    Text(
                        text = "POPULAR SOCIAL MEDIA APPS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TotalSecurityPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "One-Tap Add Social Accounts",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            )
                            Text(
                                text = "Facebook, Instagram, X, TikTok, Telegram, WhatsApp & more",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (brand in BrandIconHelper.POPULAR_SOCIAL_BRANDS) {
                                    BrandQuickChip(
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
                        color = TextDarkSecondary,
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

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun BrandQuickChip(
    brand: BrandInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("quick_brand_${brand.id}"),
        colors = CardDefaults.cardColors(containerColor = CleanSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = brand.iconRes),
                contentDescription = brand.name,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = brand.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextDarkPrimary
                )
            )
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
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
                        color = TextDarkPrimary
                    )
                )
                Text(
                    text = option.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                )
                if (option.category == VaultCategory.SOCIAL) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val socialIcons = listOf(
                            R.drawable.ic_brand_facebook,
                            R.drawable.ic_brand_instagram,
                            R.drawable.ic_brand_x_twitter,
                            R.drawable.ic_brand_tiktok,
                            R.drawable.ic_brand_telegram
                        )
                        for (iconRes in socialIcons) {
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "+ more",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextDarkMuted, fontSize = 10.sp)
                        )
                    }
                } else if (option.category == VaultCategory.CRYPTO) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val cryptoIcons = listOf(
                            R.drawable.ic_brand_bitcoin,
                            R.drawable.ic_brand_ethereum,
                            R.drawable.ic_brand_metamask,
                            R.drawable.ic_brand_binance
                        )
                        for (iconRes in cryptoIcons) {
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Seed phrase & wallets",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextDarkMuted, fontSize = 10.sp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = option.examples,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkMuted,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Select",
                tint = TextDarkMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
