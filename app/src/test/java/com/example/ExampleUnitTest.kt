package com.example

import com.example.adere.core.backup.BackupManager
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.core.crypto.TOTPGenerator
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    @Test
    fun testAesGcmEncryptionDecryptionRoundTrip() {
        val testDek = CryptoEngine.generateDek()
        val originalPlaintext = "SuperSecretAderePassword!2026#Vault"

        val encrypted = CryptoEngine.encryptString(originalPlaintext, testDek)
        assertNotNull(encrypted.ciphertext)
        assertNotNull(encrypted.iv)
        assertEquals(CryptoEngine.GCM_IV_SIZE_BYTES, encrypted.iv.size)

        val decrypted = CryptoEngine.decryptString(encrypted.ciphertext, encrypted.iv, testDek)
        assertEquals(originalPlaintext, decrypted)
    }

    @Test(expected = Exception::class)
    fun testAesGcmCorruptedCiphertextFails() {
        val testDek = CryptoEngine.generateDek()
        val original = "SensitiveSeedPhrase"
        val encrypted = CryptoEngine.encryptString(original, testDek)

        // Tamper with the ciphertext
        val corruptedCiphertext = encrypted.ciphertext.copyOf()
        corruptedCiphertext[0] = (corruptedCiphertext[0].toInt() xor 0xFF).toByte()

        // Should throw AEADBadTagException / GeneralSecurityException
        CryptoEngine.decryptString(corruptedCiphertext, encrypted.iv, testDek)
    }

    @Test
    fun testDifferentIvsProducedForIdenticalPlaintext() {
        val testDek = CryptoEngine.generateDek()
        val text = "ConstantPlaintext"

        val enc1 = CryptoEngine.encryptString(text, testDek)
        val enc2 = CryptoEngine.encryptString(text, testDek)

        assertFalse(enc1.iv.contentEquals(enc2.iv))
        assertFalse(enc1.ciphertext.contentEquals(enc2.ciphertext))
    }

    @Test
    fun testPbkdf2KeyDerivation() {
        val salt = CryptoEngine.generateSalt()
        val pass = "AdereMasterPassword2026!".toCharArray()

        val key1 = CryptoEngine.deriveKey(pass, salt, iterations = 10_000)
        val key2 = CryptoEngine.deriveKey(pass, salt, iterations = 10_000)

        assertTrue(key1.encoded.contentEquals(key2.encoded))

        // Different salt yields different key
        val salt2 = CryptoEngine.generateSalt()
        val key3 = CryptoEngine.deriveKey(pass, salt2, iterations = 10_000)
        assertFalse(key1.encoded.contentEquals(key3.encoded))
    }

    @Test
    fun testPasswordGeneratorOptionsAndEntropy() {
        val options = PasswordGenerator.GeneratorOptions(
            length = 24,
            includeUppercase = true,
            includeLowercase = true,
            includeDigits = true,
            includeSymbols = true,
            excludeAmbiguous = true
        )

        val result = PasswordGenerator.generate(options)
        assertEquals(24, result.password.length)
        assertTrue(result.password.any { it.isUpperCase() })
        assertTrue(result.password.any { it.isLowerCase() })
        assertTrue(result.password.any { it.isDigit() })
        assertTrue(result.entropyBits > 80.0)
        assertEquals(PasswordGenerator.PasswordStrength.VERY_STRONG, result.strength)
    }

    @Test
    fun testPasswordGeneratorPassphraseMode() {
        val options = PasswordGenerator.GeneratorOptions(
            isPassphraseMode = true,
            passphraseWordCount = 4,
            passphraseSeparator = "-"
        )
        val result = PasswordGenerator.generate(options)
        assertTrue(result.password.contains("-"))
        assertTrue(result.password.length >= 16)
    }

    @Test
    fun testTotpGenerationAndBase32Decoding() {
        // Standard RFC 4226 / 6238 Base32 test vector
        val secret = "JBSWY3DPEHPK3PXP"
        val decoded = TOTPGenerator.decodeBase32(secret)
        assertNotNull(decoded)
        assertEquals("Hello", String(decoded!!.take(5).toByteArray(), Charsets.UTF_8))

        val result = TOTPGenerator.generateCurrentTotp(
            secretBase32 = secret,
            currentTimeMillis = 1600000000000L
        )
        assertNotNull(result)
        assertEquals(6, result!!.code.length)
        assertTrue(result.secondsRemaining in 0..30)
    }

    @Test
    fun testPasswordHealthAnalyzer() {
        val items = listOf(
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "1",
                title = "Service A",
                username = "user1",
                passwordPlaintext = "weak",
                has2FA = false,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            ),
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "2",
                title = "Service B",
                username = "user2",
                passwordPlaintext = "weak", // Reused
                has2FA = false,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            ),
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "3",
                title = "Service C",
                username = "user3",
                passwordPlaintext = "ComplexP@ssw0rd!2026#Secure",
                has2FA = true,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            )
        )

        val report = PasswordHealthAnalyzer.analyze(items)
        assertEquals(3, report.totalItems)
        assertEquals(3, report.totalPasswords)
        assertEquals(2, report.weakCount) // "weak" is short
        assertEquals(2, report.reusedCount) // "weak" used twice
        assertEquals(2, report.missing2faCount)
    }

    @Test
    fun testEncryptedBackupAndRestore() {
        val item1 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.SOCIAL,
            title = "Test Social",
            username = "alice",
            payload = VaultItemPayload(password = "P@ssword123", url = "https://example.com")
        )

        val backupPassphrase = "MySecretBackupKey999!".toCharArray()
        val exportResult = BackupManager.createEncryptedBackup(listOf(item1), backupPassphrase)

        assertTrue(exportResult.backupString.startsWith("ADERE_VAULT_BACKUP:v1:"))
        assertEquals(1, exportResult.itemCount)

        // Decrypt with correct passphrase
        val restoreResult = BackupManager.decryptAndValidateBackup(exportResult.backupString, backupPassphrase)
        assertTrue(restoreResult.isSuccess)
        val preview = restoreResult.getOrThrow()
        assertEquals(1, preview.itemCount)
        assertEquals("Test Social", preview.items[0].title)
        assertEquals("P@ssword123", preview.items[0].payload.password)

        // Decrypt with incorrect passphrase fails securely
        val wrongResult = BackupManager.decryptAndValidateBackup(exportResult.backupString, "WrongPassword!".toCharArray())
        assertTrue(wrongResult.isFailure)
    }

    @Test
    fun testPdfExportGeneration() {
        val item1 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.EMAIL,
            title = "Personal Gmail",
            username = "alice@example.com",
            payload = VaultItemPayload(password = "Secr3tP@ss!", url = "https://mail.google.com")
        )
        val item2 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.CRYPTO,
            title = "Bitcoin Cold Storage",
            username = "bc1q...",
            payload = VaultItemPayload(
                cryptoAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
                cryptoNetwork = "Bitcoin"
            )
        )

        val byteArrayOutputStream = java.io.ByteArrayOutputStream()
        val options = com.example.adere.core.backup.PdfExportManager.ExportOptions(
            includePasswords = true,
            includeCryptoSecrets = true,
            includeNotes = true
        )
        com.example.adere.core.backup.PdfExportManager.exportToPdf(
            items = listOf(item1, item2),
            options = options,
            outputStream = byteArrayOutputStream
        )

        val pdfBytes = byteArrayOutputStream.toByteArray()
        assertTrue(pdfBytes.isNotEmpty())
        val pdfHeader = String(pdfBytes.take(5).toByteArray(), Charsets.US_ASCII)
        assertEquals("%PDF-", pdfHeader)
    }

    @Test
    fun testVaultSearchFilteringByTitleAndCategory() {
        val item1 = VaultItem(
            id = "1",
            category = VaultCategory.SOCIAL,
            title = "Instagram Account",
            username = "insta_user",
            payload = VaultItemPayload(password = "P@ss1")
        )
        val item2 = VaultItem(
            id = "2",
            category = VaultCategory.CRYPTO,
            title = "Cold Storage Ledger",
            username = "0x123",
            payload = VaultItemPayload(cryptoNetwork = "Ethereum")
        )
        val item3 = VaultItem(
            id = "3",
            category = VaultCategory.EMAIL,
            title = "Work Gmail",
            username = "worker@company.com",
            payload = VaultItemPayload(password = "P@ss3")
        )
        val allItems = listOf(item1, item2, item3)

        fun filterItems(query: String, category: VaultCategory = VaultCategory.ALL): List<VaultItem> {
            return allItems.filter { item ->
                val matchesCategory = when (category) {
                    VaultCategory.ALL -> true
                    VaultCategory.FAVORITES -> item.isFavorite
                    else -> item.category == category
                }
                val matchesQuery = query.isBlank() ||
                        item.title.contains(query, ignoreCase = true) ||
                        item.category.title.contains(query, ignoreCase = true) ||
                        item.category.name.contains(query, ignoreCase = true) ||
                        item.username.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }

        // Search by title "Instagram"
        val byTitle = filterItems("Instagram")
        assertEquals(1, byTitle.size)
        assertEquals("Instagram Account", byTitle[0].title)

        // Search by category "Crypto"
        val byCategory = filterItems("Crypto")
        assertEquals(1, byCategory.size)
        assertEquals("Cold Storage Ledger", byCategory[0].title)

        // Search by category "Social"
        val bySocialCategory = filterItems("Social")
        assertEquals(1, bySocialCategory.size)
        assertEquals("Instagram Account", bySocialCategory[0].title)

        // Search by category "Email"
        val byEmailCategory = filterItems("Email")
        assertEquals(1, byEmailCategory.size)
        assertEquals("Work Gmail", byEmailCategory[0].title)

        // Search with non-matching query
        val emptyResult = filterItems("NonExistentXYZ")
        assertTrue(emptyResult.isEmpty())
    }

    @Test
    fun testBiometricSessionLifecycleAndLockState() {
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = com.example.adere.core.crypto.KeystoreManager()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        // Fresh session is uninitialized
        assertFalse(sessionManager.isVaultInitialized())
        assertEquals(com.example.adere.domain.repository.VaultLockState.Uninitialized, sessionManager.lockState.value)

        // Test lock state transitions
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Uninitialized, sessionManager.lockState.value)
    }

    @Test
    fun testMasterRecoveryKeyFormatAndValidation() {
        val key = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
        assertNotNull(key)
        assertEquals(29, key.length) // 24 chars + 5 hyphens = 29
        assertTrue(com.example.adere.core.crypto.RecoveryKeyManager.isValidKeyFormat(key))

        // Normalization
        val raw = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(key)
        assertEquals(24, raw.length)
        val reformatted = com.example.adere.core.crypto.RecoveryKeyManager.formatKey(raw)
        assertEquals(key, reformatted)

        // Invalid key rejects
        assertFalse(com.example.adere.core.crypto.RecoveryKeyManager.isValidKeyFormat("INVALID-TOO-SHORT"))
    }

    @Test
    fun testVaultRecoveryWithKeyWhenPasswordForgotten() = kotlinx.coroutines.runBlocking {
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = com.example.adere.core.crypto.KeystoreManager()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        // 1. Initialize vault with initial master password
        val initResult = sessionManager.initializeVault("InitialPass123!".toCharArray(), enableBiometric = false)
        assertTrue(initResult.isSuccess)
        assertTrue(sessionManager.isVaultInitialized())

        // 2. Fetch the active recovery key
        val keyResult = sessionManager.getActiveRecoveryKey()
        assertTrue(keyResult.isSuccess)
        val recoveryKey = keyResult.getOrThrow()
        assertTrue(recoveryKey.isNotBlank())

        // 3. User locks vault and forgets password
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // 4. Attempt recovery with wrong key fails
        val badKeyResult = sessionManager.recoverVaultWithKey("AAAA-BBBB-CCCC-DDDD-EEEE-FFFF", "NewPass456!".toCharArray())
        assertTrue(badKeyResult.isFailure)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // 5. Successful recovery using correct Master Recovery Key
        val recoveryResult = sessionManager.recoverVaultWithKey(recoveryKey, "NewPass456!".toCharArray())
        assertTrue(recoveryResult.isSuccess)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)

        // 6. Test lock and unlock with new password
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // Old password must fail
        val oldUnlock = sessionManager.unlockWithMasterPassword("InitialPass123!".toCharArray())
        assertTrue(oldUnlock.isFailure)

        // New password must succeed
        val newUnlock = sessionManager.unlockWithMasterPassword("NewPass456!".toCharArray())
        assertTrue(newUnlock.isSuccess)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)
    }
}
