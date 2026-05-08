package com.abplus.triaheads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abplus.triaheads.ui.screens.LicensesScreen
import com.abplus.triaheads.ui.screens.NoteEditScreen
import com.abplus.triaheads.ui.screens.NoteListScreen
import com.abplus.triaheads.ui.theme.TriAheadsTheme
import com.abplus.triaheads.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private object Route {
        const val NOTE_LIST = "note_list"
        const val LICENSES = "licenses"
        const val NOTE_ID_ARG = "noteId"
        const val NOTE_EDIT = "note_edit/{$NOTE_ID_ARG}"
        const val NOTE_EDIT_BASE = "note_edit"
    }

    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TriAheadsTheme {
                val navController = rememberNavController()
                val notes by noteViewModel.notes.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = Route.NOTE_LIST
                ) {
                    composable(Route.NOTE_LIST) {
                        NoteListScreen(
                            noteViewModel = noteViewModel,
                            onEditClick = { note ->
                                navController.navigate("${Route.NOTE_EDIT_BASE}/${note.id}")
                            },
                            onLicensesClick = {
                                navController.navigate(Route.LICENSES)
                            }
                        )
                    }
                    composable(Route.LICENSES) {
                        LicensesScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Route.NOTE_EDIT,
                        arguments = listOf(
                            navArgument(Route.NOTE_ID_ARG) { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getLong(Route.NOTE_ID_ARG)
                        val note = notes.firstOrNull { it.id == noteId }

                        if (note == null) {
                            LaunchedEffect(noteId) {
                                navController.popBackStack()
                            }
                            return@composable
                        }

                        NoteEditScreen(
                            note = note,
                            onUpdateClick = { updatedNote ->
                                noteViewModel.updateNote(updatedNote)
                                navController.popBackStack()
                            },
                            onCancelClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
