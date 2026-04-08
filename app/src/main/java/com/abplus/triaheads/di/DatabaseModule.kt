package com.abplus.triaheads.di

import android.content.Context
import androidx.room.Room
import com.abplus.triaheads.data.NoteRepository
import com.abplus.triaheads.data.NoteRepositorySwitching
import com.abplus.triaheads.data.auth.AuthStateObserver
import com.abplus.triaheads.data.auth.FirebaseAuthStateObserver
import com.abplus.triaheads.data.local.NoteDao
import com.abplus.triaheads.data.local.NoteRepositoryLocal
import com.abplus.triaheads.data.local.TriAheadsDatabase
import com.abplus.triaheads.data.remote.NoteRepositoryFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

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
    fun provideAuthStateObserver(
        observer: FirebaseAuthStateObserver
    ): AuthStateObserver = observer

    @Provides
    @Singleton
    fun provideNoteRepository(
        switchingRepository: NoteRepositorySwitching
    ): NoteRepository = switchingRepository
}
