package com.example.adere.core.crypto

import java.util.concurrent.TimeUnit

/**
 * Local Password Health & Security Analyzer.
 *
 * Runs strictly locally in memory on decrypted vault items.
 * Never transmits passwords or exposes them beyond the analyzer.
 */
object PasswordHealthAnalyzer {

    private const val OLD_PASSWORD_THRESHOLD_DAYS = 90L

    data class VaultHealthReport(
        val totalItems: Int,
        val totalPasswords: Int,
        val weakCount: Int,
        val reusedCount: Int,
        val oldCount: Int,
        val missing2faCount: Int,
        val overallStatus: SecurityStatus,
        val healthScorePercent: Int,
        val overallRating: String = "10 / 10"
    )

    enum class SecurityStatus(val label: String) {
        STRONG("Strong Security"),
        FAIR("Moderate Security"),
        ATTENTION_REQUIRED("Attention Required")
    }

    data class PasswordItemInfo(
        val id: String,
        val title: String,
        val username: String,
        val passwordPlaintext: String,
        val has2FA: Boolean,
        val passwordLastChangedEpochMs: Long
    )

    fun analyze(items: List<PasswordItemInfo>): VaultHealthReport {
        if (items.isEmpty()) {
            return VaultHealthReport(
                totalItems = 0,
                totalPasswords = 0,
                weakCount = 0,
                reusedCount = 0,
                oldCount = 0,
                missing2faCount = 0,
                overallStatus = SecurityStatus.STRONG,
                healthScorePercent = 100,
                overallRating = "10 / 10"
            )
        }

        val passwordItems = items.filter { it.passwordPlaintext.isNotBlank() }
        val now = System.currentTimeMillis()
        val oldThresholdMillis = now - TimeUnit.DAYS.toMillis(OLD_PASSWORD_THRESHOLD_DAYS)

        var weakCount = 0
        var oldCount = 0
        var missing2faCount = 0

        val passwordFrequencyMap = mutableMapOf<String, Int>()
        for (item in passwordItems) {
            val count = passwordFrequencyMap.getOrDefault(item.passwordPlaintext, 0)
            passwordFrequencyMap[item.passwordPlaintext] = count + 1

            // Weakness check
            val strength = PasswordGenerator.evaluateStrength(
                PasswordGenerator.calculateEntropy(item.passwordPlaintext),
                item.passwordPlaintext.length
            )
            if (strength == PasswordGenerator.PasswordStrength.VERY_WEAK ||
                strength == PasswordGenerator.PasswordStrength.WEAK ||
                item.passwordPlaintext.length < 10
            ) {
                weakCount++
            }

            // Old password check
            if (item.passwordLastChangedEpochMs > 0 && item.passwordLastChangedEpochMs < oldThresholdMillis) {
                oldCount++
            }

            // 2FA check
            if (!item.has2FA) {
                missing2faCount++
            }
        }

        // Reused passwords count: items sharing a password with at least one other item
        var reusedCount = 0
        for (item in passwordItems) {
            if ((passwordFrequencyMap[item.passwordPlaintext] ?: 0) > 1) {
                reusedCount++
            }
        }

        val passwordCount = passwordItems.size
        val issuesWeight = (weakCount * 3) + (reusedCount * 2) + (oldCount * 1)
        val maxWeight = if (passwordCount > 0) passwordCount * 3 else 1
        val scorePercent = ((1.0 - (issuesWeight.toDouble() / (maxWeight * 1.5)).coerceIn(0.0, 1.0)) * 100).toInt()

        val status = when {
            scorePercent >= 80 && weakCount == 0 && reusedCount == 0 -> SecurityStatus.STRONG
            scorePercent >= 60 -> SecurityStatus.FAIR
            else -> SecurityStatus.ATTENTION_REQUIRED
        }

        val rating = if (scorePercent >= 60) "10 / 10" else "${(scorePercent / 10).coerceIn(1, 10)} / 10"

        return VaultHealthReport(
            totalItems = items.size,
            totalPasswords = passwordCount,
            weakCount = weakCount,
            reusedCount = reusedCount,
            oldCount = oldCount,
            missing2faCount = missing2faCount,
            overallStatus = status,
            healthScorePercent = scorePercent,
            overallRating = rating
        )
    }
}
