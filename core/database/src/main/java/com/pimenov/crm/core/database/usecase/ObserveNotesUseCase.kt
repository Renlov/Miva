package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class ObserveNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.observeAll()
}
