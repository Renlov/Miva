package com.pimenov.crm.feature.notes.api

object NotesNavigationRoute {
    const val ROOT = "notes"
    const val EDITOR = "note_editor/{noteId}"
    fun editorRoute(noteId: Long = -1L) = "note_editor/$noteId"
}
