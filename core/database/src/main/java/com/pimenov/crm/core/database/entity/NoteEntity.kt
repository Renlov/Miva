package com.pimenov.crm.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pimenov.crm.core.database.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val images: String = ""
) {
    fun toDomain(): Note = Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPinned = isPinned,
        images = if (images.isBlank()) emptyList() else images.split(",")
    )

    companion object {
        fun fromDomain(note: Note): NoteEntity = NoteEntity(
            id = note.id,
            title = note.title,
            content = note.content,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            isPinned = note.isPinned,
            images = note.images.joinToString(",")
        )
    }
}
