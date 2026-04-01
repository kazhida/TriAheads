package com.abplus.triaheads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.abplus.triaheads.ui.screens.NoteListScreen
import com.abplus.triaheads.ui.theme.TriAheadsTheme
import com.abplus.triaheads.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TriAheadsTheme {
                NoteListScreen(noteViewModel = noteViewModel)
            }
        }
    }
}
