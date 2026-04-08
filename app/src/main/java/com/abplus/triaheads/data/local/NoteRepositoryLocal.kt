package com.abplus.triaheads.data.local

import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.data.NoteRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class NoteRepositoryLocal @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override suspend fun insert(note: NoteEntity) {
        noteDao.insert(note)
    }

    override suspend fun delete(note: NoteEntity) {
        noteDao.delete(note)
    }

    override suspend fun update(note: NoteEntity) {
        noteDao.update(note.copy(updatedAt = System.currentTimeMillis()))
    }

    override fun observeAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.observeAllNotes()
    }

    override suspend fun getAllNotes(): List<NoteEntity> {
        return noteDao.getAllNotes()
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        return noteDao.getNoteById(id)
    }
}
