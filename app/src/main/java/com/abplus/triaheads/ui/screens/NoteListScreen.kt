package com.abplus.triaheads.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.data.NoteRepository
import com.abplus.triaheads.ui.components.NoteList
import kotlinx.coroutines.launch

@Composable
fun NoteListScreen(
    noteRepository: NoteRepository
) {
    var notes by remember { mutableStateOf(emptyList<NoteEntity>()) }
    val coroutineScope = rememberCoroutineScope()
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult

        val speechText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
            ?.trim()
            .orEmpty()

        if (speechText.isBlank()) return@rememberLauncherForActivityResult

        val note = buildNoteFromSpeechResult(speechText)
        coroutineScope.launch {
            noteRepository.insert(note)
            notes = noteRepository.getAllNotes()
        }
    }

    LaunchedEffect(Unit) {
        notes = noteRepository.getAllNotes()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onFabClick(speechLauncher::launch) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    )
    { innerPadding ->
        NoteList(notes = notes, modifier = Modifier.padding(innerPadding))
    }
}


fun onFabClick(
    launchSpeechIntent: (Intent) -> Unit
) {
    launchSpeechIntent(
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "話しかけてメモを追加")
        }
    )
}

fun buildNoteFromSpeechResult(
    speechText: String
): NoteEntity {
    return NoteEntity(content = speechText)
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    NoteList(
        notes = listOf(
            NoteEntity(id = 1, content = "Sample note")
        )
    )
}
