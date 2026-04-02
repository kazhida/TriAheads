package com.abplus.triaheads.ui.screens

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.triaheads.R
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.ui.theme.BlueGrey40
import com.abplus.triaheads.ui.components.NoteList
import com.abplus.triaheads.ui.theme.FabColor
import com.abplus.triaheads.ui.theme.White
import com.abplus.triaheads.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    noteViewModel: NoteViewModel,
    onEditClick: (NoteEntity) -> Unit
) {
    val context = LocalContext.current
    val chooserTitle = stringResource(id = R.string.share_note_chooser_title)
    val deleteDialogTitle = stringResource(id = R.string.delete_note_dialog_title)
    val deleteDialogMessage = stringResource(id = R.string.delete_note_dialog_message)
    val okLabel = stringResource(id = R.string.ok)
    val cancelLabel = stringResource(id = R.string.cancel)
    val notes by noteViewModel.notes.collectAsState()
    var notePendingDeletion by remember { mutableStateOf<NoteEntity?>(null) }
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult

        val speechText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
            ?.trim()
            .orEmpty()

        noteViewModel.addNoteFromSpeech(speechText)
    }
    LaunchedEffect(noteViewModel) {
        noteViewModel.shareRequests.collect { note ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, note.content)
            }
            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val backgroundBitmap = remember(context) {
            runCatching {
                context.assets.open("images/wp001.jpg").use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }.getOrNull()
        }

        backgroundBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White.copy(alpha = 0.75f))
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_title)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlueGrey40,
                        titleContentColor = White,
                        actionIconContentColor = White
                    ),
                    navigationIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_appbar),
                            contentDescription = "App icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(36.dp).padding(start = 8.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = FabColor,
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
            NoteList(
                notes = notes,
                onShareClick = noteViewModel::shareNote,
                onEditClick = onEditClick,
                onDeleteClick = { selectedNote -> notePendingDeletion = selectedNote },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    notePendingDeletion?.let { note ->
        AlertDialog(
            onDismissRequest = { notePendingDeletion = null },
            title = { Text(text = deleteDialogTitle) },
            text = { Text(text = deleteDialogMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteViewModel.deleteNote(note)
                        notePendingDeletion = null
                    }
                ) {
                    Text(text = okLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { notePendingDeletion = null }) {
                    Text(text = cancelLabel)
                }
            }
        )
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

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    NoteList(
        notes = listOf(
            NoteEntity(id = 1, content = "Sample note")
        )
    )
}
