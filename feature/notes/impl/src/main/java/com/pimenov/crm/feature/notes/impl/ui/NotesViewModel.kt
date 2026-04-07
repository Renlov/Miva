package com.pimenov.crm.feature.notes.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.usecase.DeleteNoteUseCase
import com.pimenov.crm.core.database.usecase.GetNoteByIdUseCase
import com.pimenov.crm.core.database.usecase.ObserveNotesUseCase
import com.pimenov.crm.core.database.usecase.SaveNoteUseCase
import com.pimenov.crm.core.database.usecase.SearchNotesUseCase
import com.pimenov.crm.core.database.usecase.ToggleNotePinUseCase
import com.pimenov.uikit.UNDO_TIMEOUT_SECONDS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PendingNoteDelete(
    val note: Note,
    val remainingSeconds: Int = UNDO_TIMEOUT_SECONDS
)

class NotesViewModel(
    private val observeNotes: ObserveNotesUseCase,
    private val searchNotes: SearchNotesUseCase,
    private val getNoteById: GetNoteByIdUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    private val toggleNotePin: ToggleNotePinUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _pendingDelete = MutableStateFlow<PendingNoteDelete?>(null)
    val pendingDelete = _pendingDelete.asStateFlow()

    private var deleteJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) observeNotes()
        else searchNotes(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun requestDelete(note: Note) {
        // Finalize any previous pending delete
        finalizePendingDelete()

        _pendingDelete.value = PendingNoteDelete(note = note)
        deleteJob = viewModelScope.launch {
            deleteNote.invoke(note.id)

            for (i in UNDO_TIMEOUT_SECONDS downTo 1) {
                _pendingDelete.value = _pendingDelete.value?.copy(remainingSeconds = i)
                delay(1_000)
            }

            _pendingDelete.value = null
        }
    }

    fun undoDelete() {
        val pending = _pendingDelete.value ?: return
        deleteJob?.cancel()
        deleteJob = null
        _pendingDelete.value = null

        viewModelScope.launch {
            saveNote.invoke(pending.note)
        }
    }

    fun dismissDelete() {
        deleteJob?.cancel()
        deleteJob = null
        _pendingDelete.value = null
    }

    suspend fun getNote(id: Long): Note? = getNoteById(id)

    fun saveNote(note: Note) {
        viewModelScope.launch { saveNote.invoke(note) }
    }

    fun togglePin(noteId: Long) {
        viewModelScope.launch { toggleNotePin(noteId) }
    }

    private fun finalizePendingDelete() {
        deleteJob?.cancel()
        deleteJob = null
        _pendingDelete.value = null
    }
}
