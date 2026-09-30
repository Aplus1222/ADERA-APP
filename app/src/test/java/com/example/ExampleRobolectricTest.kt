package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.adere.data.local.AdereDatabase
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.data.local.VaultItemEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var inMemoryDb: AdereDatabase

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, AdereDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        inMemoryDb.close()
    }

    @Test
    fun readAppNameFromContext() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Adere", appName)
    }

    @Test
    fun testVaultConfigStorePreferences() {
        val configStore = VaultConfigStore(context)
        configStore.resetVault()

        assertFalse(configStore.isInitialized)
        configStore.isInitialized = true
        configStore.masterSaltBase64 = "testSalt123"
        configStore.autoLockSeconds = 300

        assertTrue(configStore.isInitialized)
        assertEquals("testSalt123", configStore.masterSaltBase64)
        assertEquals(300, configStore.autoLockSeconds)
    }

    @Test
    fun testRoomDatabaseEncryptedItemStorage() = runBlocking {
        val dao = inMemoryDb.vaultDao()
        val item = VaultItemEntity(
            id = "test-uuid-1",
            category = "SOCIAL",
            title = "Instagram",
            username = "photouser",
            encryptedPayloadBase64 = "aGVsbG9fd29ybGRfY2lwaGVydGV4dA==",
            ivBase64 = "MTIzNDU2Nzg5MDEy",
            isFavorite = true,
            createdAt = 1000L,
            updatedAt = 2000L,
            passwordLastChanged = 1000L
        )

        dao.insert(item)

        val retrieved = dao.getItemById("test-uuid-1")
        assertNotNull(retrieved)
        assertEquals("Instagram", retrieved!!.title)
        assertEquals("photouser", retrieved.username)
        assertEquals("aGVsbG9fd29ybGRfY2lwaGVydGV4dA==", retrieved.encryptedPayloadBase64)
        assertTrue(retrieved.isFavorite)

        dao.updateFavorite("test-uuid-1", false)
        val updated = dao.getItemById("test-uuid-1")
        assertFalse(updated!!.isFavorite)

        dao.deleteById("test-uuid-1")
        assertEquals(0, dao.getItemCount())
    }

    @Test
    fun testRoomVaultPdfExporterDirectFromDatabase() = runBlocking {
        val dao = inMemoryDb.vaultDao()
        val dek = com.example.adere.core.crypto.CryptoEngine.generateDek()

        // Create an encrypted entity in Room
        val payload = com.example.adere.domain.model.VaultItemPayload(
            password = "SuperSecretPassword123!",
            url = "https://github.com",
            notes = "Emergency recovery item"
        )
        val encrypted = com.example.adere.core.crypto.CryptoEngine.encryptString(payload.toJson(), dek)
        val entity = VaultItemEntity(
            id = "item-id-42",
            category = "CODE_REPOSITORY",
            title = "GitHub",
            username = "octocat",
            encryptedPayloadBase64 = encrypted.toBase64Ciphertext(),
            ivBase64 = encrypted.toBase64Iv(),
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            passwordLastChanged = System.currentTimeMillis()
        )
        dao.insert(entity)

        val exporter = com.example.adere.core.backup.RoomVaultPdfExporter(dao)
        val outStream = java.io.ByteArrayOutputStream()
        val options = com.example.adere.core.backup.PdfExportManager.ExportOptions(
            includePasswords = true,
            includeNotes = true
        )

        val result = exporter.exportVaultToPdf(dek, options, outStream)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        assertTrue(outStream.size() > 0)
    }
}
