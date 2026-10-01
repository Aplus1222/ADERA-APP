package com.example.adere.domain.repository

import android.util.Base64
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.core.crypto.KeystoreManager
import com.example.adere.data.local.VaultConfigStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Arrays

sealed interface VaultLockState {
    object Uninitialized : VaultLockState
    object Locked : VaultLockState
    object Unlocked : VaultLockState
}

/**
 * Manages vault lock state and ephemeral Data Encryption Key (DEK) lifecycle.
 * Plaintext keys are wiped from memory on lock.
 */
class VaultSessionManager(
    private val configStore: VaultConfigStore,
    private val keystoreManager: KeystoreManager
) {

    private val _lockState = MutableStateFlow<VaultLockState>(
        if (!configStore.isInitialized) VaultLockState.Uninitialized else VaultLockState.Locked
    )
    val lockState: StateFlow<VaultLockState> = _lockState.asStateFlow()

    private var inMemoryDek: ByteArray? = null
    private var lastActiveTime: Long = System.currentTimeMillis()

    fun isVaultInitialized(): Boolean = configStore.isInitialized

    fun isBiometricEnabled(): Boolean = configStore.isBiometricEnabled && keystoreManager.hasBiometricKey()

    fun getActiveDek(): ByteArray? = inMemoryDek

    fun recordInteraction() {
        lastActiveTime = System.currentTimeMillis()
    }

    fun checkAutoLock() {
        if (_lockState.value != VaultLockState.Unlocked) return
        val timeoutSeconds = configStore.autoLockSeconds
        if (timeoutSeconds <= 0) {
            // Immediate lock on background check
            lock()
            return
        }
        val elapsed = (System.currentTimeMillis() - lastActiveTime) / 1000
        if (elapsed >= timeoutSeconds) {
            lock()
        }
    }

    /**
     * Initializes a brand new vault with the chosen Master Password.
     */
    suspend fun initializeVault(masterPassword: CharArray, enableBiometric: Boolean): Result<Unit> = withContext(Dispatchers.Default) {
        return@withContext try {
            val salt = CryptoEngine.generateSalt()
            val masterKey = CryptoEngine.deriveKey(masterPassword, salt)
            val newDek = CryptoEngine.generateDek()

            // Encrypt DEK with Master Key
            val encryptedDek = CryptoEngine.encrypt(newDek, masterKey)

            configStore.masterSaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(salt)
            configStore.encryptedDekBase64 = encryptedDek.toBase64Ciphertext()
            configStore.encryptedDekIvBase64 = encryptedDek.toBase64Iv()

            // Generate and protect Master Recovery Key
            val recoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
            val normalizedRecoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(recoveryKey)
            val recoverySalt = CryptoEngine.generateSalt()
            val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedRecoveryKey.toCharArray(), recoverySalt)
            val recoveryEncryptedDek = CryptoEngine.encrypt(newDek, recoveryDerivedKey)
            val storedRecoveryKeyEnc = CryptoEngine.encryptString(recoveryKey, newDek)

            configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
            configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
            configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
            configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
            configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
            configStore.hasRecoveryKey = true

            if (enableBiometric) {
                try {
                    val biometricEncrypted = keystoreManager.encryptDek(newDek)
                    configStore.biometricEncryptedDekBase64 = biometricEncrypted.toBase64Ciphertext()
                    configStore.biometricDekIvBase64 = biometricEncrypted.toBase64Iv()
                    configStore.isBiometricEnabled = true
                } catch (e: Exception) {
                    configStore.isBiometricEnabled = false
                }
            } else {
                configStore.isBiometricEnabled = false
                keystoreManager.deleteBiometricKey()
            }

            configStore.isInitialized = true
            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L

            // Retain active DEK in memory
            inMemoryDek = newDek.clone()
            lastActiveTime = System.currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Unlocks the vault using the Master Password.
     */
    suspend fun unlockWithMasterPassword(masterPassword: CharArray): Result<Unit> = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        if (now < configStore.lockoutUntilMs) {
            val remainingSec = ((configStore.lockoutUntilMs - now) / 1000).coerceAtLeast(1)
            return@withContext Result.failure(IllegalStateException("Vault locked due to multiple failed attempts. Wait $remainingSec seconds or reset."))
        }

        return@withContext try {
            val salt = com.example.adere.core.crypto.Base64Codec.decode(configStore.masterSaltBase64)
            val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.encryptedDekBase64)
            val iv = com.example.adere.core.crypto.Base64Codec.decode(configStore.encryptedDekIvBase64)

            val masterKey = CryptoEngine.deriveKey(masterPassword, salt)
            val decryptedDek = CryptoEngine.decrypt(ciphertext, iv, masterKey)

            // Successfully unlocked
            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L
            inMemoryDek = decryptedDek
            lastActiveTime = System.currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            // Auto-provision Master Recovery Key for legacy vaults if missing
            if (!configStore.hasRecoveryKey || configStore.recoveryEncryptedDekBase64.isBlank()) {
                try {
                    val recoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
                    val normalizedKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(recoveryKey)
                    val recoverySalt = CryptoEngine.generateSalt()
                    val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedKey.toCharArray(), recoverySalt)
                    val recoveryEncryptedDek = CryptoEngine.encrypt(decryptedDek, recoveryDerivedKey)
                    val storedRecoveryKeyEnc = CryptoEngine.encryptString(recoveryKey, decryptedDek)

                    configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
                    configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
                    configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
                    configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
                    configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
                    configStore.hasRecoveryKey = true
                } catch (_: Exception) {}
            }

            Result.success(Unit)
        } catch (e: Exception) {
            // Increment failed attempts and rate limit if necessary
            val attempts = configStore.failedAttempts + 1
            configStore.failedAttempts = attempts
            if (attempts >= 5) {
                configStore.lockoutUntilMs = System.currentTimeMillis() + (30_000L * (attempts - 4))
            }
            Result.failure(IllegalArgumentException("Incorrect master password."))
        }
    }

    /**
     * Unlocks the vault using Biometrics via Android Keystore.
     */
    fun unlockWithBiometric(): Result<Unit> {
        if (!configStore.isBiometricEnabled) {
            return Result.failure(IllegalStateException("Biometric unlock is not enabled."))
        }

        return try {
            val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.biometricEncryptedDekBase64)
            val iv = com.example.adere.core.crypto.Base64Codec.decode(configStore.biometricDekIvBase64)

            val decryptResult = keystoreManager.decryptDek(ciphertext, iv)
            val decryptedDek = decryptResult.getOrThrow()

            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L
            inMemoryDek = decryptedDek
            lastActiveTime = System.currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Toggles biometric unlock on or off using active in-memory DEK.
     */
    fun setBiometricEnabled(enabled: Boolean): Result<Unit> {
        val dek = inMemoryDek ?: return Result.failure(IllegalStateException("Vault must be unlocked to modify biometrics."))
        return try {
            if (enabled) {
                val biometricEncrypted = keystoreManager.encryptDek(dek)
                configStore.biometricEncryptedDekBase64 = biometricEncrypted.toBase64Ciphertext()
                configStore.biometricDekIvBase64 = biometricEncrypted.toBase64Iv()
                configStore.isBiometricEnabled = true
            } else {
                keystoreManager.deleteBiometricKey()
                configStore.isBiometricEnabled = false
                configStore.biometricEncryptedDekBase64 = ""
                configStore.biometricDekIvBase64 = ""
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Changes Master Password without having to re-encrypt every item in the vault.
     * Simply re-encrypts the active DEK with the new derived Master Key.
     */
    suspend fun changeMasterPassword(oldPassword: CharArray, newPassword: CharArray): Result<Unit> = withContext(Dispatchers.Default) {
        val dek = inMemoryDek ?: return@withContext Result.failure(IllegalStateException("Vault is locked."))
        // Verify old password first
        val unlockTest = unlockWithMasterPassword(oldPassword)
        if (unlockTest.isFailure) return@withContext unlockTest

        return@withContext try {
            val newSalt = CryptoEngine.generateSalt()
            val newMasterKey = CryptoEngine.deriveKey(newPassword, newSalt)
            val encryptedDek = CryptoEngine.encrypt(dek, newMasterKey)

            configStore.masterSaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(newSalt)
            configStore.encryptedDekBase64 = encryptedDek.toBase64Ciphertext()
            configStore.encryptedDekIvBase64 = encryptedDek.toBase64Iv()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the decrypted Master Recovery Key for the user to view or backup.
     * Vault must be unlocked.
     */
    fun getActiveRecoveryKey(): Result<String> {
        val dek = inMemoryDek ?: return Result.failure(IllegalStateException("Vault must be unlocked to view recovery key."))
        return try {
            if (!configStore.hasRecoveryKey || configStore.storedRecoveryKeyEncBase64.isBlank()) {
                // Generate and store on the fly
                val newKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
                val normalizedKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(newKey)
                val recoverySalt = CryptoEngine.generateSalt()
                val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedKey.toCharArray(), recoverySalt)
                val recoveryEncryptedDek = CryptoEngine.encrypt(dek, recoveryDerivedKey)
                val storedRecoveryKeyEnc = CryptoEngine.encryptString(newKey, dek)

                configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
                configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
                configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
                configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
                configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
                configStore.hasRecoveryKey = true

                Result.success(newKey)
            } else {
                val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.storedRecoveryKeyEncBase64)
                val iv = com.example.adere.core.crypto.Base64Codec.decode(configStore.storedRecoveryKeyIvBase64)
                val decrypted = CryptoEngine.decryptString(ciphertext, iv, dek)
                Result.success(decrypted)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Regenerates a new Master Recovery Key, re-encrypting the active DEK.
     */
    suspend fun regenerateRecoveryKey(): Result<String> = withContext(Dispatchers.Default) {
        val dek = inMemoryDek ?: return@withContext Result.failure(IllegalStateException("Vault must be unlocked to regenerate recovery key."))
        return@withContext try {
            val newRecoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
            val normalizedKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(newRecoveryKey)
            val recoverySalt = CryptoEngine.generateSalt()
            val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedKey.toCharArray(), recoverySalt)
            val recoveryEncryptedDek = CryptoEngine.encrypt(dek, recoveryDerivedKey)
            val storedRecoveryKeyEnc = CryptoEngine.encryptString(newRecoveryKey, dek)

            configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
            configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
            configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
            configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
            configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
            configStore.hasRecoveryKey = true

            Result.success(newRecoveryKey)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Emergency vault recovery if the Master Password is forgotten.
     * Derives DEK from Recovery Key, generates new Master Password key, and re-encrypts DEK.
     */
    suspend fun recoverVaultWithKey(recoveryKeyInput: String, newMasterPassword: CharArray): Result<Unit> = withContext(Dispatchers.Default) {
        val normalized = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(recoveryKeyInput)
        if (!com.example.adere.core.crypto.RecoveryKeyManager.isValidKeyFormat(normalized)) {
            return@withContext Result.failure(IllegalArgumentException("Invalid recovery key format. Expected 24 alphanumeric characters."))
        }

        if (!configStore.hasRecoveryKey || configStore.recoveryEncryptedDekBase64.isBlank()) {
            return@withContext Result.failure(IllegalStateException("No recovery key found for this vault."))
        }

        return@withContext try {
            val recoverySalt = com.example.adere.core.crypto.Base64Codec.decode(configStore.recoverySaltBase64)
            val recoveryCiphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.recoveryEncryptedDekBase64)
            val recoveryIv = com.example.adere.core.crypto.Base64Codec.decode(configStore.recoveryEncryptedDekIvBase64)

            // Derive key from Recovery Key
            val recoveryDerivedKey = CryptoEngine.deriveKey(normalized.toCharArray(), recoverySalt)
            val recoveredDek = CryptoEngine.decrypt(recoveryCiphertext, recoveryIv, recoveryDerivedKey)

            // Successfully decrypted DEK! Re-encrypt with new Master Password
            val newMasterSalt = CryptoEngine.generateSalt()
            val newMasterKey = CryptoEngine.deriveKey(newMasterPassword, newMasterSalt)
            val newEncryptedDek = CryptoEngine.encrypt(recoveredDek, newMasterKey)

            configStore.masterSaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(newMasterSalt)
            configStore.encryptedDekBase64 = newEncryptedDek.toBase64Ciphertext()
            configStore.encryptedDekIvBase64 = newEncryptedDek.toBase64Iv()

            // Update stored recovery key ciphertext with recovered DEK
            val storedRecoveryKeyEnc = CryptoEngine.encryptString(com.example.adere.core.crypto.RecoveryKeyManager.formatKey(normalized), recoveredDek)
            configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
            configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()

            // Re-encrypt Biometric DEK if biometric was enabled
            if (configStore.isBiometricEnabled) {
                try {
                    val biometricEncrypted = keystoreManager.encryptDek(recoveredDek)
                    configStore.biometricEncryptedDekBase64 = biometricEncrypted.toBase64Ciphertext()
                    configStore.biometricDekIvBase64 = biometricEncrypted.toBase64Iv()
                } catch (_: Exception) {
                    configStore.isBiometricEnabled = false
                }
            }

            // Reset lockouts and attempts
            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L

            inMemoryDek = recoveredDek
            lastActiveTime = System.currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Incorrect Master Recovery Key. Verification failed."))
        }
    }

    /**
     * Locks vault immediately and scrubs in-memory DEK.
     */
    fun lock() {
        inMemoryDek?.let {
            Arrays.fill(it, 0.toByte())
        }
        inMemoryDek = null
        if (configStore.isInitialized) {
            _lockState.value = VaultLockState.Locked
        } else {
            _lockState.value = VaultLockState.Uninitialized
        }
    }
}
