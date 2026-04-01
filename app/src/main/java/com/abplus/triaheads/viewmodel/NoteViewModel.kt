package com.abplus.triaheads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes.asStateFlow()
    private val _shareRequests = MutableSharedFlow<NoteEntity>(extraBufferCapacity = 1)
    val shareRequests: SharedFlow<NoteEntity> = _shareRequests.asSharedFlow()

    init {
        loadNotes()
    }

    fun addNoteFromSpeech(speechText: String) {
        val content = speechText.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            noteRepository.insert(NoteEntity(content = content))
            loadNotes()
        }
    }

    fun shareNote(note: NoteEntity) {
        _shareRequests.tryEmit(note)
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.update(note)
            loadNotes()
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.delete(note)
            loadNotes()
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _notes.value = noteRepository.getAllNotes()
        }
    }
}
