package com.abplus.triaheads.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HeadEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TriAheadsDatabase : RoomDatabase() {
    abstract fun headDao(): HeadDao
}
