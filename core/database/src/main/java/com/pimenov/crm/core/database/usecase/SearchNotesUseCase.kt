package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class SearchNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(query: String): Flow<List<Note>> = repository.search(query)
}
