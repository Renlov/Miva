package com.pimenov.crm.data.sync

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.repository.NoteRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class NoteSyncRepository(
    private val noteRepository: NoteRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun notesCollection(uid: String) =
        firestore.collection("users").document(uid).collection("notes")

    suspend fun sync() {
        // Wait briefly for auth state if currentUser is null (race after sign-in)
        var uid = auth.currentUser?.uid
        if (uid == null) {
            Log.d("NoteSync", "currentUser is null, waiting for auth state...")
            uid = awaitAuthUid()
        }
        if (uid == null) {
            throw IllegalStateException("User not authenticated")
        }
        Log.d("NoteSync", "Starting sync for uid=$uid")

        val localNotes = noteRepository.getAll()
        Log.d("NoteSync", "Local notes: ${localNotes.size}")
        val remoteNotes = fetchRemoteNotes(uid)
        Log.d("NoteSync", "Remote notes: ${remoteNotes.size}")

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

    private suspend fun awaitAuthUid(): String? {
        return withTimeoutOrNull(3000L) {
            suspendCancellableCoroutine { cont ->
                val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    val uid = firebaseAuth.currentUser?.uid
                    if (uid != null) {
                        cont.resume(uid)
                    }
                }
                cont.invokeOnCancellation { auth.removeAuthStateListener(listener) }
                auth.addAuthStateListener(listener)
            }
        }
    }
}
