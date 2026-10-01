package com.example.adere.core.crypto

import java.security.SecureRandom

/**
 * Manages Master Recovery Keys for zero-knowledge vault restoration.
 *
 * Uses an unambiguous 32-character alphabet (Crockford-style Base32,
 * excluding confusing characters like 0, O, 1, I, L) to prevent transcription errors.
 *
 * Standard format: 24 characters formatted in 6 groups of 4:
 * e.g., H7M9-KW4P-9N2X-B8TF-R6Y3-V5EQ (120 bits of cryptographic entropy).
 */
object RecoveryKeyManager {

    private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private const val RAW_KEY_LENGTH = 24
    private const val CHUNK_SIZE = 4

    private val secureRandom = SecureRandom()

    /**
     * Generates a fresh, cryptographically secure 24-character Master Recovery Key.
     */
    fun generateRecoveryKey(): String {
        val chars = CharArray(RAW_KEY_LENGTH)
        for (i in 0 until RAW_KEY_LENGTH) {
            val idx = secureRandom.nextInt(ALPHABET.length)
            chars[i] = ALPHABET[idx]
        }
        return formatKey(String(chars))
    }

    /**
     * Normalizes user-entered recovery key by removing spaces, hyphens, and converting to uppercase.
     */
    fun normalizeKey(input: String): String {
        return input.replace("-", "")
            .replace(" ", "")
            .trim()
            .uppercase()
    }

    /**
     * Formats a normalized 24-character string into 4-character chunks: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
     */
    fun formatKey(raw: String): String {
        val normalized = normalizeKey(raw)
        return normalized.chunked(CHUNK_SIZE).joinToString("-")
    }

    /**
     * Validates if the key matches the length and alphabet requirements.
     */
    fun isValidKeyFormat(input: String): Boolean {
        val normalized = normalizeKey(input)
        if (normalized.length != RAW_KEY_LENGTH) return false
        return normalized.all { it in ALPHABET }
    }
}
