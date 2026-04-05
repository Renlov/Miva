package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.repository.NoteRepository

class ToggleNotePinUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: Long) {
        val note = repository.getById(noteId) ?: return
        repository.save(note.copy(isPinned = !note.isPinned))
    }
}
