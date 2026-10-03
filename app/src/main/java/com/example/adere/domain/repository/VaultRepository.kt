package com.example.adere.domain.repository

import android.util.Base64
import com.example.adere.core.backup.BackupManager
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.data.local.VaultDao
import com.example.adere.data.local.VaultItemEntity
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class VaultRepository(
    private val dao: VaultDao,
    private val sessionManager: VaultSessionManager
) {

    val allItemsFlow: Flow<List<VaultItem>> = dao.getAllItemsFlow().map { entities ->
        val dek = sessionManager.getActiveDek()
        if (dek == null) {
            emptyList()
        } else {
            entities.mapNotNull { entity ->
                decryptEntity(entity, dek)
            }
        }
    }

    suspend fun getItemById(id: String): VaultItem? = withContext(Dispatchers.IO) {
        val dek = sessionManager.getActiveDek() ?: return@withContext null
        val entity = dao.getItemById(id) ?: return@withContext null
        decryptEntity(entity, dek)
    }

    suspend fun saveItem(item: VaultItem): Result<Unit> = withContext(Dispatchers.IO) {
        val dek = sessionManager.getActiveDek()
            ?: return@withContext Result.failure(IllegalStateException("Vault is locked."))

        return@withContext try {
            val payloadJson = item.payload.toJson()
            val encrypted = CryptoEngine.encryptString(payloadJson, dek)

            val entity = VaultItemEntity(
                id = item.id,
                category = item.category.name,
                title = item.title,
                username = item.username,
                encryptedPayloadBase64 = encrypted.toBase64Ciphertext(),
                ivBase64 = encrypted.toBase64Iv(),
                isFavorite = item.isFavorite,
                createdAt = item.createdAt,
                updatedAt = System.currentTimeMillis(),
                passwordLastChanged = item.passwordLastChanged
            )
            dao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.updateFavorite(id, isFavorite)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllItems(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.deleteAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllDecryptedItems(): List<VaultItem> = withContext(Dispatchers.IO) {
        val dek = sessionManager.getActiveDek() ?: return@withContext emptyList()
        val entities = dao.getAllItems()
        entities.mapNotNull { decryptEntity(it, dek) }
    }

    suspend fun computeHealthReport(): PasswordHealthAnalyzer.VaultHealthReport = withContext(Dispatchers.IO) {
        val items = getAllDecryptedItems()
        val passwordInfos = items.map { item ->
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = item.id,
                title = item.title,
                username = item.username,
                passwordPlaintext = item.payload.password,
                has2FA = item.payload.totpSecret.isNotBlank(),
                passwordLastChangedEpochMs = item.passwordLastChanged
            )
        }
        PasswordHealthAnalyzer.analyze(passwordInfos)
    }

    suspend fun exportEncryptedBackup(backupPassphrase: CharArray): Result<BackupManager.BackupExportResult> =
        withContext(Dispatchers.IO) {
            val items = getAllDecryptedItems()
            try {
                val result = BackupManager.createEncryptedBackup(items, backupPassphrase)
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun exportToPdf(
        options: com.example.adere.core.backup.PdfExportManager.ExportOptions,
        outputStream: java.io.OutputStream
    ): Result<Int> = withContext(Dispatchers.IO) {
        val dek = sessionManager.getActiveDek()
            ?: return@withContext Result.failure(IllegalStateException("Vault is locked."))
        val items = getAllDecryptedItems()
        try {
            com.example.adere.core.backup.PdfExportManager.exportToPdf(items, options, outputStream)
            try {
                outputStream.flush()
            } catch (_: Exception) {}
            Result.success(items.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFromEncryptedBackup(
        backupContent: String,
        backupPassphrase: CharArray
    ): Result<Int> = withContext(Dispatchers.IO) {
        val dek = sessionManager.getActiveDek()
            ?: return@withContext Result.failure(IllegalStateException("Vault must be unlocked to restore backup."))

        val previewResult = BackupManager.decryptAndValidateBackup(backupContent, backupPassphrase)
        if (previewResult.isFailure) {
            return@withContext Result.failure(previewResult.exceptionOrNull() ?: Exception("Restore failed"))
        }

        val restoredItems = previewResult.getOrThrow().items
        try {
            val entities = restoredItems.map { item ->
                val encrypted = CryptoEngine.encryptString(item.payload.toJson(), dek)
                VaultItemEntity(
                    id = item.id,
                    category = item.category.name,
                    title = item.title,
                    username = item.username,
                    encryptedPayloadBase64 = encrypted.toBase64Ciphertext(),
                    ivBase64 = encrypted.toBase64Iv(),
                    isFavorite = item.isFavorite,
                    createdAt = item.createdAt,
                    updatedAt = item.updatedAt,
                    passwordLastChanged = item.passwordLastChanged
                )
            }
            dao.insertAll(entities)
            Result.success(entities.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun decryptEntity(entity: VaultItemEntity, dek: ByteArray): VaultItem? {
        return try {
            val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(entity.encryptedPayloadBase64)
            val iv = com.example.adere.core.crypto.Base64Codec.decode(entity.ivBase64)
            val jsonString = CryptoEngine.decryptString(ciphertext, iv, dek)
            val payload = VaultItemPayload.fromJson(jsonString)

            VaultItem(
                id = entity.id,
                category = VaultCategory.fromName(entity.category),
                title = entity.title,
                username = entity.username,
                payload = payload,
                isFavorite = entity.isFavorite,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                passwordLastChanged = entity.passwordLastChanged
            )
        } catch (e: Exception) {
            null
        }
    }
}
