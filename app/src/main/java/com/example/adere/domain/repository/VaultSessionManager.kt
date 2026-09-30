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
