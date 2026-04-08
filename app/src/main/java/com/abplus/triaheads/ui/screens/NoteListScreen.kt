package com.abplus.triaheads.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abplus.triaheads.R
import com.abplus.triaheads.data.NoteEntity
import com.abplus.triaheads.ui.theme.BlueGrey40
import com.abplus.triaheads.ui.components.NoteList
import com.abplus.triaheads.ui.theme.FabColor
import com.abplus.triaheads.ui.theme.White
import com.abplus.triaheads.viewmodel.NoteViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.initialize
import coil.compose.AsyncImage
import java.io.File

private const val WALLPAPER_FILE_NAME = "wallpaper.jpg"

@SuppressLint("AutoboxingStateCreation")
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
    val menuDescription = stringResource(id = R.string.menu)
    val changeWallpaperLabel = stringResource(id = R.string.change_wallpaper)
    val loginLabel = stringResource(id = R.string.login)
    val logoutLabel = stringResource(id = R.string.logout)
    val loginSuccessLabel = stringResource(id = R.string.login_success)
    val accountCreatedLabel = stringResource(id = R.string.login_new_account_created)
    val loginFailedLabel = stringResource(id = R.string.login_failed)
    val firebaseNotConfiguredLabel = stringResource(id = R.string.firebase_not_configured)
    val loginCanceledLabel = stringResource(id = R.string.login_canceled)
    val googleClientIdMissingLabel = stringResource(id = R.string.google_client_id_missing)
    val logoutSuccessLabel = stringResource(id = R.string.logout_success)
    val logoutFailedLabel = stringResource(id = R.string.logout_failed)
    val notes by noteViewModel.notes.collectAsState<List<NoteEntity>>()
    var notePendingDeletion by remember { mutableStateOf<NoteEntity?>(null) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var wallpaperVersion by remember { mutableStateOf(0) }
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
    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val wallpaperSaved = runCatching {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val outputFile = File(context.filesDir, WALLPAPER_FILE_NAME)
                outputFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                true
            } ?: false
        }.getOrDefault(false)

        if (wallpaperSaved) {
            wallpaperVersion += 1
        } else {
            Toast.makeText(context, R.string.wallpaper_set_failed, Toast.LENGTH_SHORT).show()
        }
    }
    val googleSignInLauncher = rememberLauncherForActivityResult<Intent, ActivityResult>(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            Toast.makeText(context, loginCanceledLabel, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        val account = runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
        }.getOrNull()

        if (account?.idToken.isNullOrBlank()) {
            Toast.makeText(context, loginFailedLabel, Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }

        loginToFirebaseWithGoogleAccount(
            context = context,
            account = account,
            loginSuccessLabel = loginSuccessLabel,
            accountCreatedLabel = accountCreatedLabel,
            loginFailedLabel = loginFailedLabel,
            firebaseNotConfiguredLabel = firebaseNotConfiguredLabel
        )
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
        val backgroundBitmap = remember(context, wallpaperVersion) {
            loadBackgroundBitmap(context)
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
                .background(color = White.copy(alpha = 0.5f))
        )

        val user = FirebaseAuth.getInstance().currentUser
        val isLoggedIn = user != null

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
                        IconButton(onClick = { isMenuExpanded = true }) {
                            if (isLoggedIn) {
                                AsyncImage(
                                    model = user.photoUrl.toString(),
                                    contentDescription = "remote icon",
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = menuDescription
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = changeWallpaperLabel) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Wallpaper,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    wallpaperPickerLauncher.launch("image/*")
                                }
                            )
                            if (isLoggedIn) {
                                DropdownMenuItem(
                                    text = { Text(text = logoutLabel) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        logoutFromFirebaseAndGoogle(
                                            context = context,
                                            logoutSuccessLabel = logoutSuccessLabel,
                                            logoutFailedLabel = logoutFailedLabel
                                        )
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(text = loginLabel) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Login,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        startGoogleLogin(
                                            context = context,
                                            googleSignInLauncher = googleSignInLauncher::launch,
                                            googleClientIdMissingLabel = googleClientIdMissingLabel,
                                            loginFailedLabel = loginFailedLabel
                                        )
                                    }
                                )
                            }
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

private fun startGoogleLogin(
    context: Context,
    googleSignInLauncher: (Intent) -> Unit,
    googleClientIdMissingLabel: String,
    loginFailedLabel: String
) {
    val activity = context as? Activity ?: run {
        Toast.makeText(context, loginFailedLabel, Toast.LENGTH_LONG).show()
        return
    }
    val webClientId = resolveWebClientId(context)
    if (webClientId.isBlank() || webClientId == "YOUR_FIREBASE_WEB_CLIENT_ID") {
        Toast.makeText(context, googleClientIdMissingLabel, Toast.LENGTH_LONG).show()
        return
    }

    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(/* serverClientId = */ webClientId)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(activity, options)
    googleSignInLauncher(googleSignInClient.signInIntent)
}

private fun logoutFromFirebaseAndGoogle(
    context: Context,
    logoutSuccessLabel: String,
    logoutFailedLabel: String
) {
    FirebaseAuth.getInstance().signOut()

    val webClientId = resolveWebClientId(context)
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(/* serverClientId = */ webClientId)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, options)
    googleSignInClient.signOut()
        .addOnSuccessListener {
            Toast.makeText(context, logoutSuccessLabel, Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, logoutFailedLabel, Toast.LENGTH_LONG).show()
        }
}

private fun resolveWebClientId(context: Context): String {
    val generatedClientIdResId = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )
    return if (generatedClientIdResId != 0) {
        context.getString(generatedClientIdResId)
    } else {
        context.getString(R.string.firebase_web_client_id)
    }
}

private fun loginToFirebaseWithGoogleAccount(
    context: Context,
    account: GoogleSignInAccount,
    loginSuccessLabel: String,
    accountCreatedLabel: String,
    loginFailedLabel: String,
    firebaseNotConfiguredLabel: String
) {
    Firebase.initialize(context)
    val auth = runCatching { FirebaseAuth.getInstance() }
        .getOrElse {
            Toast.makeText(context, firebaseNotConfiguredLabel, Toast.LENGTH_LONG).show()
            return
        }
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
    auth.signInWithCredential(credential)
        .addOnSuccessListener { authResult ->
            val message = if (authResult.additionalUserInfo?.isNewUser == true) {
                accountCreatedLabel
            } else {
                loginSuccessLabel
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            val message = it.localizedMessage ?: loginFailedLabel
            Toast.makeText(context, "$loginFailedLabel: $message", Toast.LENGTH_LONG).show()
        }
}


private fun loadBackgroundBitmap(context: Context): Bitmap? {
    val customWallpaperFile = File(context.filesDir, WALLPAPER_FILE_NAME)
    if (customWallpaperFile.exists()) {
        BitmapFactory.decodeFile(customWallpaperFile.absolutePath)?.let { return it }
    }

    return runCatching {
        context.assets.open("images/wp001.jpg").use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }.getOrNull()
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
