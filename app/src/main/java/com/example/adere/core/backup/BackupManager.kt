package com.example.adere.core.backup

import android.util.Base64
import com.example.adere.core.crypto.Base64Codec
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Adere Encrypted Backup and Restore Manager (.adere format).
 *
 * Implements authenticated encrypted backups. Plaintext secrets never leave memory.
 */
object BackupManager {

    private const val BACKUP_MAGIC_PREFIX = "ADERE_VAULT_BACKUP"
    private const val FORMAT_VERSION = "v1"

    data class BackupExportResult(
        val backupString: String,
        val itemCount: Int
    )

    data class RestorePreview(
        val items: List<VaultItem>,
        val itemCount: Int,
        val version: String
    )

    /**
     * Exports a list of decrypted vault items to an encrypted .adere format
     * using a dedicated user-provided backup passphrase.
     */
    fun createEncryptedBackup(
        items: List<VaultItem>,
        backupPassphrase: CharArray
    ): BackupExportResult {
        val rootJson = JSONObject()
        rootJson.put("version", FORMAT_VERSION)
        rootJson.put("exportedAt", System.currentTimeMillis())

        val itemsArray = JSONArray()
        for (item in items) {
            val itemJson = JSONObject()
            itemJson.put("id", item.id)
            itemJson.put("category", item.category.name)
            itemJson.put("title", item.title)
            itemJson.put("username", item.username)
            itemJson.put("payload", item.payload.toJson())
            itemJson.put("isFavorite", item.isFavorite)
            itemJson.put("createdAt", item.createdAt)
            itemJson.put("updatedAt", item.updatedAt)
            itemJson.put("passwordLastChanged", item.passwordLastChanged)
            itemsArray.put(itemJson)
        }
        rootJson.put("items", itemsArray)

        val plaintextBytes = rootJson.toString().toByteArray(Charsets.UTF_8)
        val salt = CryptoEngine.generateSalt()
        val derivedKey = CryptoEngine.deriveKey(backupPassphrase, salt)
        val encrypted = CryptoEngine.encrypt(plaintextBytes, derivedKey)

        val saltB64 = Base64Codec.encode(salt)
        val ivB64 = encrypted.toBase64Iv()
        val cipherB64 = encrypted.toBase64Ciphertext()

        val fullBackupContent = "$BACKUP_MAGIC_PREFIX:$FORMAT_VERSION:$saltB64:$ivB64:$cipherB64"
        return BackupExportResult(fullBackupContent, items.size)
    }

    /**
     * Decrypts and parses an encrypted backup string.
     * Throws an exception with safe user message on invalid password, corruption, or version mismatch.
     */
    fun decryptAndValidateBackup(
        backupContent: String,
        backupPassphrase: CharArray
    ): Result<RestorePreview> {
        return try {
            val cleanContent = backupContent.trim().replace("\r", "").replace("\n", "")
            val parts = cleanContent.split(":")
            if (parts.size != 5 || parts[0] != BACKUP_MAGIC_PREFIX) {
                return Result.failure(IllegalArgumentException("Invalid or unsupported Adere backup file format. Expected 5 colon-separated sections."))
            }

            val version = parts[1]
            if (version != FORMAT_VERSION) {
                return Result.failure(IllegalArgumentException("Unsupported backup format version: $version"))
            }

            val salt = Base64Codec.decode(parts[2])
            val iv = Base64Codec.decode(parts[3])
            val ciphertext = Base64Codec.decode(parts[4])

            val derivedKey = CryptoEngine.deriveKey(backupPassphrase, salt)
            val decryptedBytes = try {
                CryptoEngine.decrypt(ciphertext, iv, derivedKey)
            } catch (e: Exception) {
                return Result.failure(IllegalArgumentException("Incorrect passphrase or corrupted backup payload."))
            }

            val decryptedJson = String(decryptedBytes, Charsets.UTF_8)
            val root = JSONObject(decryptedJson)
            val itemsArray = root.optJSONArray("items") ?: JSONArray()
            val itemsList = mutableListOf<VaultItem>()

            for (i in 0 until itemsArray.length()) {
                val obj = itemsArray.getJSONObject(i)
                val payloadObj = obj.opt("payload")
                val payloadJson = when (payloadObj) {
                    is JSONObject -> payloadObj.toString()
                    is String -> payloadObj
                    else -> "{}"
                }
                val item = VaultItem(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    category = VaultCategory.fromName(obj.optString("category", "OTHER")),
                    title = obj.optString("title", "Untitled Secret"),
                    username = obj.optString("username", ""),
                    payload = VaultItemPayload.fromJson(payloadJson),
                    isFavorite = obj.optBoolean("isFavorite", false),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                    passwordLastChanged = obj.optLong("passwordLastChanged", System.currentTimeMillis())
                )
                itemsList.add(item)
            }

            Result.success(RestorePreview(itemsList, itemsList.size, version))
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Unable to restore backup: ${e.message ?: "Invalid payload or wrong passphrase"}"))
        }
    }

    /**
     * Exports aggregated vault items into a formatted PDF document.
     * All sensitive data remains encrypted in storage and is only decrypted in-memory
     * during temporary generation.
     */
    fun exportToPdf(
        items: List<VaultItem>,
        options: PdfExportManager.ExportOptions,
        outputStream: java.io.OutputStream
    ) {
        PdfExportManager.exportToPdf(items, options, outputStream)
    }

    /**
     * Exports encrypted vault items directly from Room database to a formatted PDF.
     * Ephemeral decryption in memory with complete buffer wiping on completion.
     */
    suspend fun exportFromRoomToPdf(
        vaultDao: com.example.adere.data.local.VaultDao,
        dek: javax.crypto.SecretKey,
        options: PdfExportManager.ExportOptions,
        outputStream: java.io.OutputStream
    ): Result<Int> {
        return RoomVaultPdfExporter(vaultDao).exportVaultToPdf(dek, options, outputStream)
    }
}
