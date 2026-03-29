package com.abplus.triaheads.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abplus.triaheads.data.NoteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteRepositoryLocalTest {
    private lateinit var database: TriAheadsDatabase
    private lateinit var repository: NoteRepositoryLocal

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            TriAheadsDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = NoteRepositoryLocal(database.noteDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetById_returnsSavedNote() = runBlocking {
        repository.insert(NoteEntity(content = "first", headId = 1L))

        val notes = repository.getAllNotes()
        assertEquals(1, notes.size)

        val saved = repository.getNoteById(notes.first().id)
        assertEquals("first", saved?.content)
        assertEquals(1L, saved?.headId)
    }

    @Test
    fun updateAndDelete_reflectsLatestState() = runBlocking {
        repository.insert(NoteEntity(content = "before", headId = 10L))
        val inserted = repository.getAllNotes().first()

        repository.update(inserted.copy(content = "after"))
        val updated = repository.getNoteById(inserted.id)
        assertEquals("after", updated?.content)

        repository.delete(updated!!)
        assertNull(repository.getNoteById(inserted.id))
    }
}
