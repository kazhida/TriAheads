package com.abplus.triaheads.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeadDao {
    @Query("SELECT * FROM heads ORDER BY id DESC")
    fun observeAll(): Flow<List<HeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(head: HeadEntity)
}
