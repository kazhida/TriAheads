package com.abplus.triaheads.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.triaheads.R
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.ui.theme.BlueGrey40
import com.abplus.triaheads.ui.theme.TriAheadsTheme
import com.abplus.triaheads.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    note: NoteEntity,
    onUpdateClick: (NoteEntity) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember(note.id, note.content) {
        mutableStateOf(
            TextFieldValue(
                text = note.content,
                selection = TextRange(note.content.length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(note.id) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_title_edit)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlueGrey40,
                    titleContentColor = White,
                    actionIconContentColor = White
                )
            )
        }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.edit_note_title),
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            val end = content.text.length
                            if (content.selection.start != end || content.selection.end != end) {
                                content = content.copy(selection = TextRange(end))
                            }
                        }
                    },
                label = { Text(text = stringResource(id = R.string.note_content_label)) },
                minLines = 2
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        onCancelClick()
                    },
                    modifier = Modifier.weight(2f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        onUpdateClick(
                            note.copy(
                                content = content.text,
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    },
                    modifier = Modifier.weight(2f),
                    enabled = content.text.isNotBlank()
                ) {
                    Text(text = stringResource(id = R.string.update))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteEditScreenPreview() {
    TriAheadsTheme {
        NoteEditScreen(
            note = NoteEntity(
                id = 1,
                content = "Sample note"
            ),
            onUpdateClick = {},
            onCancelClick = {}
        )
    }
}
