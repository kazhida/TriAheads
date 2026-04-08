package com.abplus.triaheads.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun insert(note: NoteEntity)
    suspend fun delete(note: NoteEntity)
    suspend fun update(note: NoteEntity)

    fun observeAllNotes(): Flow<List<NoteEntity>>
    suspend fun getAllNotes(): List<NoteEntity>
    suspend fun getNoteById(id: Long): NoteEntity?
}
