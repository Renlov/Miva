package com.pimenov.crm.data.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.repository.NoteRepository
import kotlinx.coroutines.tasks.await

class NoteSyncRepository(
    private val noteRepository: NoteRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun notesCollection(uid: String) =
        firestore.collection("users").document(uid).collection("notes")

    suspend fun sync() {
        val uid = auth.currentUser?.uid ?: return

        val localNotes = noteRepository.getAll()
        val remoteNotes = fetchRemoteNotes(uid)

        val remoteById = remoteNotes.associateBy { it.id }
        val localById = localNotes.associateBy { it.id }

        val allIds = localById.keys + remoteById.keys

        for (id in allIds) {
            val local = localById[id]
            val remote = remoteById[id]

            when {
                local != null && remote == null -> {
                    // Only on device — upload
                    uploadNote(uid, local)
                }
                local == null && remote != null -> {
                    // Only on server — save locally without changing updatedAt
                    noteRepository.saveExact(remote)
                }
                local != null && remote != null -> {
                    // Both exist — keep the newer one
                    if (local.updatedAt >= remote.updatedAt) {
                        uploadNote(uid, local)
                    } else {
                        noteRepository.saveExact(remote)
                    }
                }
            }
        }
    }

    private suspend fun fetchRemoteNotes(uid: String): List<Note> {
        val snapshot = notesCollection(uid).get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.getLong("id") ?: return@mapNotNull null
            val title = doc.getString("title").orEmpty()
            val content = doc.getString("content").orEmpty()
            val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
            val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
            Note(id = id, title = title, content = content, createdAt = createdAt, updatedAt = updatedAt)
        }
    }

    private suspend fun uploadNote(uid: String, note: Note) {
        val data = mapOf(
            "id" to note.id,
            "title" to note.title,
            "content" to note.content,
            "createdAt" to note.createdAt,
            "updatedAt" to note.updatedAt
        )
        notesCollection(uid).document(note.id.toString()).set(data).await()
    }
}
