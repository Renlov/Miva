package com.pimenov.crm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pimenov.crm.domain.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Note = Note(id, title, content, createdAt, updatedAt)

    companion object {
        fun fromDomain(note: Note): NoteEntity =
            NoteEntity(note.id, note.title, note.content, note.createdAt, note.updatedAt)
    }
}
