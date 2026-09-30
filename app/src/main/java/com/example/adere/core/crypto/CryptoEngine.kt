package com.example.adere.core.crypto

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Adere Cryptographic Engine
 *
 * Implements authenticated encryption using AES-256-GCM and key derivation
 * using PBKDF2-HMAC-SHA256 with 100,000 iterations and 256-bit cryptographically
 * secure random salts.
 *
 * Guaranteed invariants:
 * - Every encryption uses a fresh, unique 96-bit (12-byte) IV from SecureRandom.
 * - Authenticated tags (128-bit) verify integrity before returning plaintext.
 * - No custom or unauthenticated primitives used.
 */
object CryptoEngine {

    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val AES_GCM_CIPHER = "AES/GCM/NoPadding"
    private const val AES_KEY_ALGORITHM = "AES"

    const val PBKDF2_ITERATIONS = 100_000
    const val KEY_SIZE_BITS = 256
    const val SALT_SIZE_BYTES = 32
    const val GCM_IV_SIZE_BYTES = 12
    const val GCM_TAG_LENGTH_BITS = 128

    private val secureRandom = SecureRandom()

    fun generateRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }

    fun generateSalt(): ByteArray = generateRandomBytes(SALT_SIZE_BYTES)

    fun generateDek(): ByteArray = generateRandomBytes(KEY_SIZE_BITS / 8)

    /**
     * Derives a 256-bit AES key from a passphrase using PBKDF2-HMAC-SHA256.
     */
    fun deriveKey(passphrase: CharArray, salt: ByteArray, iterations: Int = PBKDF2_ITERATIONS): SecretKey {
        val spec = PBEKeySpec(passphrase, salt, iterations, KEY_SIZE_BITS)
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val rawBytes = factory.generateSecret(spec).encoded
        spec.clearPassword()
        return SecretKeySpec(rawBytes, AES_KEY_ALGORITHM)
    }

    data class EncryptedData(
        val ciphertext: ByteArray,
        val iv: ByteArray
    ) {
        fun toBase64Ciphertext(): String = Base64Codec.encode(ciphertext)
        fun toBase64Iv(): String = Base64Codec.encode(iv)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as EncryptedData
            if (!ciphertext.contentEquals(other.ciphertext)) return false
            if (!iv.contentEquals(other.iv)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = ciphertext.contentHashCode()
            result = 31 * result + iv.contentHashCode()
            return result
        }
    }

    /**
     * Encrypts plaintext bytes with AES-256-GCM.
     * Always generates a fresh 12-byte IV.
     */
    fun encrypt(plaintext: ByteArray, secretKey: SecretKey): EncryptedData {
        val iv = generateRandomBytes(GCM_IV_SIZE_BYTES)
        val cipher = Cipher.getInstance(AES_GCM_CIPHER)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val ciphertext = cipher.doFinal(plaintext)
        return EncryptedData(ciphertext, iv)
    }

    /**
     * Decrypts ciphertext with AES-256-GCM using the provided IV.
     * Throws AEADBadTagException / GeneralSecurityException if modified or incorrect key.
     */
    fun decrypt(ciphertext: ByteArray, iv: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(AES_GCM_CIPHER)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(ciphertext)
    }

    /**
     * Convenience encryption for UTF-8 string with AES-256 SecretKeySpec
     */
    fun encryptString(plaintext: String, rawKey: ByteArray): EncryptedData {
        val key = SecretKeySpec(rawKey, AES_KEY_ALGORITHM)
        return encrypt(plaintext.toByteArray(Charsets.UTF_8), key)
    }

    /**
     * Convenience decryption returning UTF-8 string
     */
    fun decryptString(ciphertext: ByteArray, iv: ByteArray, rawKey: ByteArray): String {
        val key = SecretKeySpec(rawKey, AES_KEY_ALGORITHM)
        val decryptedBytes = decrypt(ciphertext, iv, key)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
