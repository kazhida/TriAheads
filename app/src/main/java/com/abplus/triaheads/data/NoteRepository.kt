package com.abplus.triaheads.data

interface NoteRepository {

    suspend fun insert(note: NoteEntity)
    suspend fun delete(note: NoteEntity)
    suspend fun update(note: NoteEntity)

    suspend fun getAllNotes(): List<NoteEntity>
    suspend fun getNoteById(id: Long): NoteEntity?
}
