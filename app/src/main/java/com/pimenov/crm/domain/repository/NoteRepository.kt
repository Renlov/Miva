package com.pimenov.crm.domain.repository

import com.pimenov.crm.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeAll(): Flow<List<Note>>
    fun search(query: String): Flow<List<Note>>
    suspend fun getById(id: Long): Note?
    suspend fun save(note: Note): Long
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}
