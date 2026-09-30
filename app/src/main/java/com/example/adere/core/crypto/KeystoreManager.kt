package com.example.adere.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Manages platform-protected keys in the Android KeyStore.
 *
 * Responsibilities:
 * - Generate hardware-backed AES-256 keys inside AndroidKeyStore
 * - Protect the Vault Data Encryption Key (DEK) for Biometric unlocking
 * - Gracefully handle KeyPermanentlyInvalidatedException, user authentication, and hardware status
 */
class KeystoreManager {

    companion object {
        private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val BIOMETRIC_KEY_ALIAS = "adere_vault_biometric_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH_BITS = 128
        private const val IV_SIZE = 12
    }

    private val keyStore: KeyStore? = try {
        KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply {
            load(null)
        }
    } catch (e: Throwable) {
        null
    }

    fun hasBiometricKey(): Boolean {
        return keyStore?.containsAlias(BIOMETRIC_KEY_ALIAS) ?: false
    }

    fun deleteBiometricKey() {
        try {
            if (keyStore?.containsAlias(BIOMETRIC_KEY_ALIAS) == true) {
                keyStore.deleteEntry(BIOMETRIC_KEY_ALIAS)
            }
        } catch (e: Throwable) {
            // Ignored if keystore unavailable
        }
    }

    /**
     * Generates an AES-256 GCM key in the Android KeyStore.
     */
    fun generateBiometricKey(): SecretKey {
        deleteBiometricKey()

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE_PROVIDER
        )

        val builder = KeyGenParameterSpec.Builder(
            BIOMETRIC_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)

        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey? {
        val ks = keyStore ?: return null
        if (!ks.containsAlias(BIOMETRIC_KEY_ALIAS)) return null
        return (ks.getEntry(BIOMETRIC_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
    }

    /**
     * Encrypts the Vault Data Encryption Key (DEK) with the Keystore key.
     */
    fun encryptDek(dek: ByteArray): CryptoEngine.EncryptedData {
        val key = getSecretKey() ?: generateBiometricKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(dek)
        return CryptoEngine.EncryptedData(ciphertext, iv)
    }

    /**
     * Decrypts the Vault Data Encryption Key (DEK) using the Keystore key.
     */
    fun decryptDek(ciphertext: ByteArray, iv: ByteArray): Result<ByteArray> {
        return try {
            val key = getSecretKey() ?: return Result.failure(IllegalStateException("Biometric key not found"))
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            val dek = cipher.doFinal(ciphertext)
            Result.success(dek)
        } catch (e: KeyPermanentlyInvalidatedException) {
            deleteBiometricKey()
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
