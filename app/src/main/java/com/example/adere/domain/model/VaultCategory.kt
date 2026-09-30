package com.example.adere.domain.model

enum class VaultCategory(
    val title: String,
    val description: String
) {
    ALL("All Items", "Everything in your vault"),
    FAVORITES("Favorites", "Starred accounts & secrets"),
    SOCIAL("Social Media", "Social accounts, logins, and recovery codes"),
    EMAIL("Email", "Personal and work email accounts"),
    BANKING("Banking", "Bank logins, cards, and customer IDs"),
    CRYPTO("Crypto Vault", "Wallets, seed phrases, and private keys"),
    WEBSITE("Websites", "Web services and portals"),
    WIFI("Wi-Fi", "Network names and access passwords"),
    IDENTITY("Identity", "Passports, IDs, and personal documents"),
    NOTES("Secure Notes", "Encrypted private memos and records"),
    TOTP_2FA("2FA / TOTP", "Two-factor authentication codes"),
    RECOVERY("Recovery Codes", "Emergency backup codes for accounts"),
    OTHER("Other", "Custom sensitive credentials");

    companion object {
        fun fromName(name: String): VaultCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}
