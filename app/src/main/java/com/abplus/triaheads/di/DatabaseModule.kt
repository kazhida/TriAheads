package com.abplus.triaheads.di

import android.content.Context
import androidx.room.Room
import com.abplus.triaheads.data.NoteRepository
import com.abplus.triaheads.data.local.NoteDao
import com.abplus.triaheads.data.local.NoteRepositoryLocal
import com.abplus.triaheads.data.local.TriAheadsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TriAheadsDatabase {
        return Room.databaseBuilder(
            context,
            TriAheadsDatabase::class.java,
            "triaheads.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(
        database: TriAheadsDatabase
    ): NoteDao = database.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(
        repositoryLocal: NoteRepositoryLocal
    ): NoteRepository = repositoryLocal
}
