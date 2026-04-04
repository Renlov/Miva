package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.repository.NoteRepository

class GetNoteByIdUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Note? = repository.getById(id)
}
