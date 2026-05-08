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
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    private val _refreshCompleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshCompleted: SharedFlow<Unit> = _refreshCompleted.asSharedFlow()
    private val _scrollToTopRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val scrollToTopRequests: SharedFlow<Unit> = _scrollToTopRequests.asSharedFlow()
    private val _scrollToTopOnNextList = MutableStateFlow(false)
    val scrollToTopOnNextList: StateFlow<Boolean> = _scrollToTopOnNextList.asStateFlow()
    private val _shareRequests = MutableSharedFlow<NoteEntity>(extraBufferCapacity = 1)
    val shareRequests: SharedFlow<NoteEntity> = _shareRequests.asSharedFlow()

    init {
        viewModelScope.launch {
            noteRepository.observeAllNotes().collect { latest ->
                _notes.value = latest
            }
        }
        refreshNotes()
    }

    fun addNoteFromSpeech(speechText: String) {
        val content = speechText.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            noteRepository.insert(NoteEntity(content = content))
            _scrollToTopRequests.tryEmit(Unit)
        }
    }

    fun shareNote(note: NoteEntity) {
        if (note.content.isBlank()) return
        _shareRequests.tryEmit(note)
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.update(note)
            _scrollToTopOnNextList.value = true
        }
    }

    fun onScrollToTopHandled() {
        _scrollToTopOnNextList.value = false
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.delete(note)
        }
    }

    fun refreshNotes() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            _notes.value = runCatching {
                noteRepository.getAllNotes()
            }.getOrElse { _notes.value }
            _isRefreshing.value = false
            _refreshCompleted.tryEmit(Unit)
        }
    }
}
