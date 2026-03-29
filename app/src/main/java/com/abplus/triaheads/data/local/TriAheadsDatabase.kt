package com.abplus.triaheads.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abplus.triaheads.data.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TriAheadsDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
