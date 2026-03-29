package com.abplus.triaheads.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abplus.triaheads.ui.components.NoteList

@Composable
fun NoteListScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = ::onFabClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    )
    { innerPadding ->
        NoteList(notes = emptyList(), modifier = Modifier.padding(innerPadding))
    }
}


fun onFabClick() {
    TODO()
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    NoteListScreen()
}
