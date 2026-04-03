package com.pimenov.crm.data.repository

import com.pimenov.crm.data.local.dao.NoteDao
import com.pimenov.crm.data.local.entity.NoteEntity
import com.pimenov.crm.domain.model.Note
import com.pimenov.crm.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(private val dao: NoteDao) : NoteRepository {

    override fun observeAll(): Flow<List<Note>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Note>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Note? =
        dao.getById(id)?.toDomain()

    override suspend fun save(note: Note): Long =
        dao.insert(NoteEntity.fromDomain(note.copy(updatedAt = System.currentTimeMillis())))

    override suspend fun delete(id: Long) = dao.delete(id)

    override suspend fun deleteAll() = dao.deleteAll()
}
