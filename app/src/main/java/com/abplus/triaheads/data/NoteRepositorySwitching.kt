package com.abplus.triaheads.data

import com.abplus.triaheads.data.auth.AuthStateObserver
import com.abplus.triaheads.data.local.NoteRepositoryLocal
import com.abplus.triaheads.data.remote.NoteRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class NoteRepositorySwitching @Inject constructor(
    private val authStateObserver: AuthStateObserver,
    private val firebaseAuth: FirebaseAuth,
    private val localRepository: NoteRepositoryLocal,
    private val firestoreRepository: NoteRepositoryFirestore
) : NoteRepository {
    override suspend fun insert(note: NoteEntity) {
        currentRepository().insert(note)
    }

    override suspend fun delete(note: NoteEntity) {
        currentRepository().delete(note)
    }

    override suspend fun update(note: NoteEntity) {
        currentRepository().update(note)
    }

    override fun observeAllNotes(): Flow<List<NoteEntity>> {
        return authStateObserver.isLoggedIn.flatMapLatest { isLoggedIn ->
            if (isLoggedIn) {
                firestoreRepository.observeAllNotes()
            } else {
                localRepository.observeAllNotes()
            }
        }
    }

    override suspend fun getAllNotes(): List<NoteEntity> {
        return currentRepository().getAllNotes()
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        return currentRepository().getNoteById(id)
    }

    private fun currentRepository(): NoteRepository {
        return if (firebaseAuth.currentUser != null) firestoreRepository else localRepository
    }
}
