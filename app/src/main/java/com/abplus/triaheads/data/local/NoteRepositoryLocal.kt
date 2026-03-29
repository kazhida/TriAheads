package com.abplus.triaheads.data.local

import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.data.NoteRepository
import javax.inject.Inject
import javax.inject.Singleton

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
        noteDao.update(note)
    }

    override suspend fun getAllNotes(): List<NoteEntity> {
        return noteDao.getAllNotes()
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        return noteDao.getNoteById(id)
    }
}
