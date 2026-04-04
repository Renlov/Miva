package com.pimenov.crm.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.usecase.DeleteNoteUseCase
import com.pimenov.crm.core.database.usecase.GetNoteByIdUseCase
import com.pimenov.crm.core.database.usecase.ObserveNotesUseCase
import com.pimenov.crm.core.database.usecase.SaveNoteUseCase
import com.pimenov.crm.core.database.usecase.SearchNotesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val observeNotes: ObserveNotesUseCase,
    private val searchNotes: SearchNotesUseCase,
    private val getNoteById: GetNoteByIdUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) observeNotes()
        else searchNotes(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch { deleteNote.invoke(id) }
    }

    suspend fun getNote(id: Long): Note? = getNoteById(id)

    fun saveNote(note: Note) {
        viewModelScope.launch { saveNote.invoke(note) }
    }
}
