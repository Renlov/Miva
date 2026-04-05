package com.pimenov.crm.core.database.repository

import com.pimenov.crm.core.database.dao.NoteDao
import com.pimenov.crm.core.database.entity.NoteEntity
import com.pimenov.crm.core.database.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NoteRepositoryImpl(private val dao: NoteDao) : NoteRepository {

    override fun observeAll(): Flow<List<Note>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Note>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<Note> =
        dao.getAll().map { it.toDomain() }

    override suspend fun getById(id: Long): Note? =
        dao.getById(id)?.toDomain()

    override suspend fun save(note: Note): Long =
        dao.insert(NoteEntity.fromDomain(note.copy(updatedAt = System.currentTimeMillis())))

    override suspend fun saveExact(note: Note): Long =
        dao.insert(NoteEntity.fromDomain(note))

    override suspend fun delete(id: Long) = dao.delete(id)

    override suspend fun deleteAll() = dao.deleteAll()
}
