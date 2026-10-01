package com.example.adere.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.adere.domain.model.VaultCategory
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight

data class BrandInfo(
    val id: String,
    val name: String,
    val defaultUrl: String,
    val category: VaultCategory,
    @DrawableRes val iconRes: Int,
    val brandColor: Color
)

object BrandIconHelper {

    val POPULAR_SOCIAL_BRANDS = listOf(
        BrandInfo("instagram", "Instagram", "https://instagram.com", VaultCategory.SOCIAL, R.drawable.ic_brand_instagram, Color(0xFFC13584)),
        BrandInfo("facebook", "Facebook", "https://facebook.com", VaultCategory.SOCIAL, R.drawable.ic_brand_facebook, Color(0xFF1877F2)),
        BrandInfo("x_twitter", "X (Twitter)", "https://x.com", VaultCategory.SOCIAL, R.drawable.ic_brand_x_twitter, Color(0xFF000000)),
        BrandInfo("tiktok", "TikTok", "https://tiktok.com", VaultCategory.SOCIAL, R.drawable.ic_brand_tiktok, Color(0xFF010101)),
        BrandInfo("whatsapp", "WhatsApp", "https://web.whatsapp.com", VaultCategory.SOCIAL, R.drawable.ic_brand_whatsapp, Color(0xFF25D366)),
        BrandInfo("telegram", "Telegram", "https://web.telegram.org", VaultCategory.SOCIAL, R.drawable.ic_brand_telegram, Color(0xFF24A1DE)),
        BrandInfo("youtube", "YouTube", "https://youtube.com", VaultCategory.SOCIAL, R.drawable.ic_brand_youtube, Color(0xFFFF0000)),
        BrandInfo("snapchat", "Snapchat", "https://snapchat.com", VaultCategory.SOCIAL, R.drawable.ic_brand_snapchat, Color(0xFFFFFC00)),
        BrandInfo("figma", "Figma", "https://figma.com", VaultCategory.WEBSITE, R.drawable.ic_brand_figma, Color(0xFFF24E1E)),
        BrandInfo("discord", "Discord", "https://discord.com", VaultCategory.SOCIAL, R.drawable.ic_brand_discord, Color(0xFF5865F2)),
        BrandInfo("linkedin", "LinkedIn", "https://linkedin.com", VaultCategory.SOCIAL, R.drawable.ic_brand_linkedin, Color(0xFF0A66C2)),
        BrandInfo("reddit", "Reddit", "https://reddit.com", VaultCategory.SOCIAL, R.drawable.ic_brand_reddit, Color(0xFFFF4500)),
        BrandInfo("google", "Google / Gmail", "https://accounts.google.com", VaultCategory.EMAIL, R.drawable.ic_brand_google, Color(0xFF4285F4)),
        BrandInfo("spotify", "Spotify", "https://spotify.com", VaultCategory.WEBSITE, R.drawable.ic_brand_spotify, Color(0xFF1ED760)),
        BrandInfo("netflix", "Netflix", "https://netflix.com", VaultCategory.WEBSITE, R.drawable.ic_brand_netflix, Color(0xFFE50914)),
        BrandInfo("github", "GitHub", "https://github.com", VaultCategory.WEBSITE, R.drawable.ic_brand_github, Color(0xFF24292F)),
        BrandInfo("apple", "Apple ID", "https://appleid.apple.com", VaultCategory.WEBSITE, R.drawable.ic_brand_apple, Color(0xFF1E293B))
    )

    val POPULAR_CRYPTO_BRANDS = listOf(
        BrandInfo("bitcoin", "Bitcoin Wallet", "https://bitcoin.org", VaultCategory.CRYPTO, R.drawable.ic_brand_bitcoin, Color(0xFFF7931A)),
        BrandInfo("ethereum", "Ethereum Wallet", "https://ethereum.org", VaultCategory.CRYPTO, R.drawable.ic_brand_ethereum, Color(0xFF627EEA)),
        BrandInfo("metamask", "MetaMask Wallet", "https://metamask.io", VaultCategory.CRYPTO, R.drawable.ic_brand_metamask, Color(0xFFE2761B)),
        BrandInfo("binance", "Binance Account", "https://binance.com", VaultCategory.CRYPTO, R.drawable.ic_brand_binance, Color(0xFFF3BA2F)),
        BrandInfo("seed_phrase", "12/24-Word Seed Phrase", "https://ethereum.org", VaultCategory.CRYPTO, R.drawable.ic_brand_ethereum, Color(0xFF10B981))
    )

    val POPULAR_EMAIL_BRANDS = listOf(
        BrandInfo("gmail", "Gmail / Google Account", "https://accounts.google.com", VaultCategory.EMAIL, R.drawable.ic_brand_google, Color(0xFF4285F4)),
        BrandInfo("outlook", "Outlook / Microsoft", "https://outlook.live.com", VaultCategory.EMAIL, R.drawable.ic_brand_google, Color(0xFF0078D4)),
        BrandInfo("apple_id", "iCloud / Apple ID", "https://appleid.apple.com", VaultCategory.EMAIL, R.drawable.ic_brand_apple, Color(0xFF1E293B))
    )

    fun resolveBrandDrawable(title: String, url: String? = null): Int? {
        val query = (title + " " + (url ?: "")).lowercase().trim()
        return when {
            query.contains("bitcoin") || query.contains("btc") -> R.drawable.ic_brand_bitcoin
            query.contains("ethereum") || query.contains("eth") -> R.drawable.ic_brand_ethereum
            query.contains("metamask") -> R.drawable.ic_brand_metamask
            query.contains("binance") || query.contains("bnb") -> R.drawable.ic_brand_binance
            query.contains("instagram") || query.contains("insta") -> R.drawable.ic_brand_instagram
            query.contains("facebook") || query.contains("fb.com") -> R.drawable.ic_brand_facebook
            query.contains("twitter") || query.contains("x.com") || query.contains("x (twitter") -> R.drawable.ic_brand_x_twitter
            query.contains("tiktok") -> R.drawable.ic_brand_tiktok
            query.contains("whatsapp") -> R.drawable.ic_brand_whatsapp
            query.contains("telegram") || query.contains("t.me") -> R.drawable.ic_brand_telegram
            query.contains("youtube") || query.contains("youtu.be") -> R.drawable.ic_brand_youtube
            query.contains("linkedin") -> R.drawable.ic_brand_linkedin
            query.contains("discord") -> R.drawable.ic_brand_discord
            query.contains("figma") -> R.drawable.ic_brand_figma
            query.contains("snapchat") || query.contains("snap") -> R.drawable.ic_brand_snapchat
            query.contains("reddit") -> R.drawable.ic_brand_reddit
            query.contains("google") || query.contains("gmail") -> R.drawable.ic_brand_google
            query.contains("spotify") -> R.drawable.ic_brand_spotify
            query.contains("netflix") -> R.drawable.ic_brand_netflix
            query.contains("github") -> R.drawable.ic_brand_github
            query.contains("apple") || query.contains("icloud") -> R.drawable.ic_brand_apple
            else -> null
        }
    }

    fun getCategoryFallbackIcon(category: VaultCategory): ImageVector {
        return when (category) {
            VaultCategory.CRYPTO -> Icons.Default.CurrencyBitcoin
            VaultCategory.EMAIL -> Icons.Default.Email
            VaultCategory.BANKING -> Icons.Default.AccountBalance
            VaultCategory.NOTES -> Icons.AutoMirrored.Filled.Note
            VaultCategory.WIFI -> Icons.Default.Wifi
            VaultCategory.TOTP_2FA -> Icons.Default.QrCode
            VaultCategory.RECOVERY -> Icons.Default.LockReset
            VaultCategory.IDENTITY -> Icons.Default.Badge
            VaultCategory.SOCIAL -> Icons.Default.Share
            VaultCategory.WEBSITE -> Icons.Default.Public
            else -> Icons.Default.Key
        }
    }
}

/**
 * Renders the real brand logo if matching a known brand (Instagram, Facebook, TikTok, etc.),
 * or falls back to the clean category-specific vector icon.
 */
@Composable
fun VaultItemAvatar(
    title: String,
    category: VaultCategory,
    url: String? = null,
    size: Dp = 42.dp,
    iconSize: Dp = 22.dp,
    cornerRadius: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    val brandDrawable = BrandIconHelper.resolveBrandDrawable(title, url)
    if (brandDrawable != null) {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = brandDrawable),
                contentDescription = title,
                modifier = Modifier.size(size)
            )
        }
    } else {
        val fallbackIcon = BrandIconHelper.getCategoryFallbackIcon(category)
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(EmeraldContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = null,
                tint = EmeraldLight,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
