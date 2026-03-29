package com.abplus.triaheads.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.ui.theme.TriAheadsTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun NoteItem(
    note: NoteEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Updated: ${note.updatedAt.toDisplayDateTime()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun Long.toDisplayDateTime(): String {
    return DateFormat.getDateTimeInstance(
        DateFormat.SHORT,
        DateFormat.SHORT
    ).format(Date(this))
}

@Preview(showBackground = true)
@Composable
private fun NoteItemPreview() {
    TriAheadsTheme {
        NoteItem(
            note = NoteEntity(
                id = 1,
                content = "This is a sample note.",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
