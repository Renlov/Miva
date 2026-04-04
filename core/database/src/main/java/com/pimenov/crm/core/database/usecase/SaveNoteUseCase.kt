package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.repository.NoteRepository

class SaveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Long = repository.save(note)
}
