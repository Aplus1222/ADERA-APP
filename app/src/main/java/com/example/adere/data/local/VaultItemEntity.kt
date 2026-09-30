package com.example.adere.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Encrypted Room entity for persistent vault items.
 *
 * All sensitive properties (password, PIN, seed phrase, notes, 2FA secrets)
 * are authenticated and encrypted with AES-256-GCM inside [encryptedPayloadBase64].
 */
@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "encrypted_payload")
    val encryptedPayloadBase64: String,

    @ColumnInfo(name = "iv")
    val ivBase64: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "password_last_changed")
    val passwordLastChanged: Long
)
