package com.abplus.triaheads.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.ui.theme.TriAheadsTheme
import com.abplus.triaheads.ui.theme.agedBlack
import java.text.DateFormat
import java.util.Date

@Composable
fun NoteItem(
    note: NoteEntity,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                Text(
                    text = note.content,
                    color = agedBlack(note.createdAt),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(modifier = Modifier.fillMaxHeight().wrapContentWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Created: ${note.createdAt.toDisplayDateTime()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                )
                Text(
                    text = "Updated: ${note.updatedAt.toDisplayDateTime()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share note"
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit note"
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete note"
                        )
                    }
                }
            }
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
            onShareClick = {},
            onEditClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
