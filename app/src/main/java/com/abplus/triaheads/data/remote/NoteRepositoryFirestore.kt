package com.abplus.triaheads.data.remote

import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.data.NoteRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

@Singleton
class NoteRepositoryFirestore @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : NoteRepository {
    override suspend fun insert(note: NoteEntity) {
        val uid = requireUserId()
        val noteId = if (note.id != 0L) note.id else generateNoteId()
        val noteToSave = note.copy(
            id = noteId,
            updatedAt = System.currentTimeMillis()
        )
        userNotes(uid)
            .document(noteId.toString())
            .set(noteToSave.toMap())
            .awaitResult()
        awaitPendingWrites()
    }

    override suspend fun delete(note: NoteEntity) {
        val uid = requireUserId()
        userNotes(uid)
            .document(note.id.toString())
            .delete()
            .awaitResult()
        awaitPendingWrites()
    }

    override suspend fun update(note: NoteEntity) {
        val uid = requireUserId()
        userNotes(uid)
            .document(note.id.toString())
            .set(note.copy(updatedAt = System.currentTimeMillis()).toMap())
            .awaitResult()
        awaitPendingWrites()
    }

    override fun observeAllNotes(): Flow<List<NoteEntity>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration: ListenerRegistration = userNotes(uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notes = snapshot?.documents
                    ?.mapNotNull { it.toNoteEntity() }
                    .orEmpty()
                trySend(notes)
            }
        awaitClose {
            registration.remove()
        }
    }.retryWhen { _, _ ->
        delay(1_000L)
        firebaseAuth.currentUser != null
    }

    override suspend fun getAllNotes(): List<NoteEntity> {
        val uid = requireUserId()
        val snapshot = userNotes(uid).get().awaitResult()
        val notes = snapshot.documents
            .mapNotNull { it.toNoteEntity() }
            .sortedByDescending { it.updatedAt }
        return notes
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        val uid = requireUserId()
        val snapshot = userNotes(uid).document(id.toString()).get().awaitResult()
        return snapshot.toNoteEntity()
    }

    private fun requireUserId(): String {
        return firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in.")
    }

    private fun userNotes(uid: String) = firestore
        .collection("users")
        .document(uid)
        .collection("notes")

    private fun generateNoteId(): Long {
        val now = System.currentTimeMillis()
        return now * 1_000 + Random.nextLong(from = 0, until = 1_000)
    }

    private suspend fun awaitPendingWrites() {
        withTimeoutOrNull(15_000L) {
            firestore.waitForPendingWrites().awaitResult()
        }
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toNoteEntity(): NoteEntity? {
    val parsedId = getLong("id") ?: this.id.toLongOrNull() ?: return null
    val content = getString("content") ?: return null
    val createdAt = getLong("createdAt") ?: System.currentTimeMillis()
    val updatedAt = getLong("updatedAt") ?: createdAt
    return NoteEntity(
        id = parsedId,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun NoteEntity.toMap(): Map<String, Any> = mapOf(
    "id" to id,
    "content" to content,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        if (continuation.isActive) continuation.resume(result)
    }
    addOnFailureListener { error ->
        if (continuation.isActive) continuation.resumeWithException(error)
    }
    addOnCanceledListener {
        continuation.cancel()
    }
}
