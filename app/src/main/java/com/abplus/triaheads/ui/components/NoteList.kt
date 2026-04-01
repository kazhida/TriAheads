package com.abplus.triaheads.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.ui.theme.TriAheadsTheme

@Composable
fun NoteList(
    notes: List<NoteEntity>,
    onShareClick: (NoteEntity) -> Unit = {},
    onEditClick: (NoteEntity) -> Unit = {},
    onDeleteClick: (NoteEntity) -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = notes,
            key = { it.id }
        ) { note ->
            NoteItem(
                note = note,
                onShareClick = { onShareClick(note) },
                onEditClick = { onEditClick(note) },
                onDeleteClick = { onDeleteClick(note) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteListPreview() {
    TriAheadsTheme {
        NoteList(
            notes = List(20) { index ->
                NoteEntity(
                    id = index.toLong() + 1L,
                    content = "Sample note #${index + 1}",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
        )
    }
}
