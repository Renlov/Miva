package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.repository.NoteRepository

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}
