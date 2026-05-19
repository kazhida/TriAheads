package com.abplus.triaheads.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.initialize
import coil.compose.AsyncImage
import java.io.File

private const val WALLPAPER_FILE_NAME = "wallpaper.jpg"
private const val TAG = "NoteListScreen"
private const val GITHUB_PROVIDER_ID = "github.com"

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    noteViewModel: NoteViewModel,
    onEditClick: (NoteEntity) -> Unit,
    onLicensesClick: () -> Unit
) {
    val context = LocalContext.current
    val chooserTitle = stringResource(id = R.string.share_note_chooser_title)
    val deleteDialogTitle = stringResource(id = R.string.delete_note_dialog_title)
    val deleteDialogMessage = stringResource(id = R.string.delete_note_dialog_message)
    val deleteAccountDialogTitle = stringResource(id = R.string.delete_account_dialog_title)
    val deleteAccountDialogMessage = stringResource(id = R.string.delete_account_dialog_message)
    val appBarTitle = stringResource(id = R.string.app_title)
    val okLabel = stringResource(id = R.string.ok)
    val cancelLabel = stringResource(id = R.string.cancel)
    val menuDescription = stringResource(id = R.string.menu)
    val changeWallpaperLabel = stringResource(id = R.string.change_wallpaper)
    val loginLabel = stringResource(id = R.string.login)
    val loginWithGoogleLabel = stringResource(id = R.string.login_with_google)
    val loginWithGithubLabel = stringResource(id = R.string.login_with_github)
    val logoutLabel = stringResource(id = R.string.logout)
    val licenseLabel = stringResource(id = R.string.license)
    val loginSuccessLabel = stringResource(id = R.string.login_success)
    val accountCreatedLabel = stringResource(id = R.string.login_new_account_created)
    val loginFailedLabel = stringResource(id = R.string.login_failed)
    val authErrorTitle = stringResource(id = R.string.auth_error_title)
    val firebaseNotConfiguredLabel = stringResource(id = R.string.firebase_not_configured)
    val loginCanceledLabel = stringResource(id = R.string.login_canceled)
    val googleSignInDeveloperErrorLabel = stringResource(id = R.string.google_sign_in_developer_error)
    val googleSignInInProgressLabel = stringResource(id = R.string.google_sign_in_in_progress)
    val googleClientIdMissingLabel = stringResource(id = R.string.google_client_id_missing)
    val githubSignInInProgressLabel = stringResource(id = R.string.github_sign_in_in_progress)
    val logoutSuccessLabel = stringResource(id = R.string.logout_success)
    val logoutFailedLabel = stringResource(id = R.string.logout_failed)
    val deleteAccountLabel = stringResource(id = R.string.delete_account)
    val deleteAccountSuccessLabel = stringResource(id = R.string.delete_account_success)
    val deleteAccountFailedLabel = stringResource(id = R.string.delete_account_failed)
    val deleteAccountReauthRequiredLabel = stringResource(id = R.string.delete_account_reauth_required)
    val deleteAccountMismatchLabel = stringResource(id = R.string.delete_account_account_mismatch)
    val notes by noteViewModel.notes.collectAsState<List<NoteEntity>>()
    val isRefreshing by noteViewModel.isRefreshing.collectAsState()
    val scrollToTopOnNextList by noteViewModel.scrollToTopOnNextList.collectAsState()
    val listState = rememberLazyListState()
    var notePendingDeletion by remember { mutableStateOf<NoteEntity?>(null) }
    var isDeleteAccountDialogVisible by remember { mutableStateOf(false) }
    var isLoginMethodDialogVisible by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isAuthOperationInProgress by remember { mutableStateOf(false) }
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
        val accountResult = runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
        }
        val account = accountResult.getOrNull()

        if (account?.idToken.isNullOrBlank()) {
            isAuthOperationInProgress = false
            val apiException = accountResult.exceptionOrNull() as? ApiException
            if (apiException != null) {
                Log.w(TAG, "Google sign-in failed: statusCode=${apiException.statusCode}", apiException)
            } else {
                Log.w(TAG, "Google sign-in failed: resultCode=${result.resultCode}, idToken is missing")
            }
            val message = googleSignInFailureMessage(
                apiException = apiException,
                resultCode = result.resultCode,
                loginCanceledLabel = loginCanceledLabel,
                loginFailedLabel = loginFailedLabel,
                googleSignInDeveloperErrorLabel = googleSignInDeveloperErrorLabel,
                googleSignInInProgressLabel = googleSignInInProgressLabel
            )
            authErrorMessage = message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }

        loginToFirebaseWithGoogleAccount(
            context = context,
            account = account,
            loginSuccessLabel = loginSuccessLabel,
            accountCreatedLabel = accountCreatedLabel,
            loginFailedLabel = loginFailedLabel,
            firebaseNotConfiguredLabel = firebaseNotConfiguredLabel,
            onLoginSuccess = {
                isAuthOperationInProgress = false
                noteViewModel.refreshNotes()
            },
            onLoginFailure = { message ->
                isAuthOperationInProgress = false
                authErrorMessage = message
            }
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
    LaunchedEffect(noteViewModel, listState) {
        noteViewModel.scrollToTopRequests.collect {
            if (listState.layoutInfo.totalItemsCount > 0) {
                listState.animateScrollToItem(0)
            }
        }
    }
    LaunchedEffect(noteViewModel, listState) {
        noteViewModel.refreshCompleted.collect {
            if (listState.layoutInfo.totalItemsCount > 0) {
                listState.animateScrollToItem(0)
            }
        }
    }
    LaunchedEffect(scrollToTopOnNextList, listState, notes.size) {
        if (!scrollToTopOnNextList) return@LaunchedEffect
        if (notes.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        noteViewModel.onScrollToTopHandled()
    }
    val auth = remember { FirebaseAuth.getInstance() }
    var user by remember { mutableStateOf(auth.currentUser) }
    DisposableEffect(auth) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(authStateListener)
        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
    val isLoggedIn = user != null

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

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            topBar = {
                TopAppBar(
                    title = { Text(text = appBarTitle) },
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
                        IconButton(
                            enabled = !isAuthOperationInProgress,
                            onClick = { isMenuExpanded = true }
                        ) {
                            if (isLoggedIn) {
                                AsyncImage(
                                    model = user?.photoUrl.toString(),
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
                                enabled = !isAuthOperationInProgress,
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
                                    enabled = !isAuthOperationInProgress,
                                    text = { Text(text = logoutLabel) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        isAuthOperationInProgress = true
                                        logoutFromFirebaseAndProviders(
                                            context = context,
                                            logoutSuccessLabel = logoutSuccessLabel,
                                            logoutFailedLabel = logoutFailedLabel,
                                            onLogoutSuccess = {
                                                isAuthOperationInProgress = false
                                                noteViewModel.refreshNotes()
                                            },
                                            onLogoutFailure = {
                                                isAuthOperationInProgress = false
                                            }
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    enabled = !isAuthOperationInProgress,
                                    text = { Text(text = deleteAccountLabel) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        isDeleteAccountDialogVisible = true
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    enabled = !isAuthOperationInProgress,
                                    text = { Text(text = loginLabel) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Login,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        isLoginMethodDialogVisible = true
                                    }
                                )
                            }
                            DropdownMenuItem(
                                enabled = !isAuthOperationInProgress,
                                text = { Text(text = licenseLabel) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    onLicensesClick()
                                }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.offset(x = -8.dp),
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
                listState = listState,
                onShareClick = noteViewModel::shareNote,
                onEditClick = onEditClick,
                onDeleteClick = { selectedNote -> notePendingDeletion = selectedNote },
                isRefreshing = isRefreshing,
                onRefresh = noteViewModel::refreshNotes,
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

    if (isDeleteAccountDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDeleteAccountDialogVisible = false },
            title = { Text(text = deleteAccountDialogTitle) },
            text = { Text(text = deleteAccountDialogMessage) },
            confirmButton = {
                TextButton(
                    enabled = !isAuthOperationInProgress,
                    onClick = {
                        isDeleteAccountDialogVisible = false
                        isAuthOperationInProgress = true
                        val deleteTargetUid = user?.uid
                        startReauthenticationAndDeleteAccount(
                            context = context,
                            deleteTargetUid = deleteTargetUid,
                            googleClientIdMissingLabel = googleClientIdMissingLabel,
                            deleteAccountFailedLabel = deleteAccountFailedLabel,
                            onDeleteSuccess = noteViewModel::refreshNotes,
                            deleteAccountSuccessLabel = deleteAccountSuccessLabel,
                            deleteAccountReauthRequiredLabel = deleteAccountReauthRequiredLabel,
                            deleteAccountMismatchLabel = deleteAccountMismatchLabel,
                            onDeleteFinished = {
                                isAuthOperationInProgress = false
                            }
                        )
                    }
                ) {
                    Text(text = okLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { isDeleteAccountDialogVisible = false }) {
                    Text(text = cancelLabel)
                }
            }
        )
    }

    if (isLoginMethodDialogVisible) {
        AlertDialog(
            onDismissRequest = { isLoginMethodDialogVisible = false },
            title = { Text(text = loginLabel) },
            text = { Text(text = stringResource(id = R.string.login_method_dialog_message)) },
            confirmButton = {
                TextButton(
                    enabled = !isAuthOperationInProgress,
                    onClick = {
                        isLoginMethodDialogVisible = false
                        isAuthOperationInProgress = true
                        startGoogleLogin(
                            context = context,
                            googleSignInLauncher = googleSignInLauncher::launch,
                            googleClientIdMissingLabel = googleClientIdMissingLabel,
                            loginFailedLabel = loginFailedLabel,
                            onLaunchFailed = {
                                isAuthOperationInProgress = false
                            }
                        )
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = loginWithGoogleLabel)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isAuthOperationInProgress,
                    onClick = {
                        isLoginMethodDialogVisible = false
                        isAuthOperationInProgress = true
                        startGithubLogin(
                            context = context,
                            loginSuccessLabel = loginSuccessLabel,
                            accountCreatedLabel = accountCreatedLabel,
                            loginFailedLabel = loginFailedLabel,
                            firebaseNotConfiguredLabel = firebaseNotConfiguredLabel,
                            githubSignInInProgressLabel = githubSignInInProgressLabel,
                            onLoginSuccess = {
                                isAuthOperationInProgress = false
                                noteViewModel.refreshNotes()
                            },
                            onLoginFailure = { message ->
                                isAuthOperationInProgress = false
                                authErrorMessage = message
                            }
                        )
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_github),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = loginWithGithubLabel)
                    }
                }
            }
        )
    }

    authErrorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { authErrorMessage = null },
            title = { Text(text = authErrorTitle) },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(onClick = { authErrorMessage = null }) {
                    Text(text = okLabel)
                }
            }
        )
    }
}

private fun startGoogleLogin(
    context: Context,
    googleSignInLauncher: (Intent) -> Unit,
    googleClientIdMissingLabel: String,
    loginFailedLabel: String,
    onLaunchFailed: () -> Unit
) {
    val activity = context as? Activity ?: run {
        Toast.makeText(context, loginFailedLabel, Toast.LENGTH_LONG).show()
        onLaunchFailed()
        return
    }
    val webClientId = resolveWebClientId(context)
    if (webClientId.isBlank() || webClientId == "YOUR_FIREBASE_WEB_CLIENT_ID") {
        Toast.makeText(context, googleClientIdMissingLabel, Toast.LENGTH_LONG).show()
        onLaunchFailed()
        return
    }

    val googleSignInClient = GoogleSignIn.getClient(activity, createGoogleSignInOptions(webClientId))
    googleSignInLauncher(googleSignInClient.signInIntent)
}

private fun startGithubLogin(
    context: Context,
    loginSuccessLabel: String,
    accountCreatedLabel: String,
    loginFailedLabel: String,
    firebaseNotConfiguredLabel: String,
    githubSignInInProgressLabel: String,
    onLoginSuccess: () -> Unit,
    onLoginFailure: (String) -> Unit
) {
    val activity = context as? Activity ?: run {
        Toast.makeText(context, loginFailedLabel, Toast.LENGTH_LONG).show()
        onLoginFailure(loginFailedLabel)
        return
    }
    Firebase.initialize(context)
    val auth = runCatching { FirebaseAuth.getInstance() }
        .getOrElse { error ->
            val message = authFailureMessage(firebaseNotConfiguredLabel, error)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onLoginFailure(message)
            return
        }
    val provider = OAuthProvider.newBuilder(GITHUB_PROVIDER_ID)
    val signInTask = auth.pendingAuthResult
        ?: auth.startActivityForSignInWithProvider(activity, provider.build())

    if (auth.pendingAuthResult != null) {
        Toast.makeText(context, githubSignInInProgressLabel, Toast.LENGTH_SHORT).show()
    }

    signInTask
        .addOnSuccessListener { authResult ->
            val message = if (authResult.additionalUserInfo?.isNewUser == true) {
                accountCreatedLabel
            } else {
                loginSuccessLabel
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
        .addOnFailureListener { error ->
            Log.w(TAG, "GitHub Firebase sign-in failed", error)
            val message = authFailureMessage(loginFailedLabel, error)
            Toast.makeText(context, "$loginFailedLabel: $message", Toast.LENGTH_LONG).show()
            onLoginFailure("$loginFailedLabel: $message")
        }
}

private fun startReauthenticationAndDeleteAccount(
    context: Context,
    deleteTargetUid: String?,
    googleClientIdMissingLabel: String,
    deleteAccountFailedLabel: String,
    deleteAccountSuccessLabel: String,
    deleteAccountReauthRequiredLabel: String,
    deleteAccountMismatchLabel: String,
    onDeleteSuccess: () -> Unit,
    onDeleteFinished: () -> Unit
) {
    if (deleteTargetUid.isNullOrBlank()) {
        Toast.makeText(context, deleteAccountFailedLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }
    val activity = context as? Activity ?: run {
        Toast.makeText(context, deleteAccountFailedLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }
    val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
        Toast.makeText(context, deleteAccountFailedLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }
    if (currentUser.hasGithubProvider()) {
        startGithubReauthentication(
            activity = activity,
            context = context,
            deleteTargetUid = deleteTargetUid,
            deleteAccountFailedLabel = deleteAccountFailedLabel,
            deleteAccountSuccessLabel = deleteAccountSuccessLabel,
            deleteAccountMismatchLabel = deleteAccountMismatchLabel,
            onDeleteSuccess = onDeleteSuccess,
            onDeleteFinished = onDeleteFinished
        )
        return
    }

    val webClientId = resolveWebClientId(context)
    if (webClientId.isBlank() || webClientId == "YOUR_FIREBASE_WEB_CLIENT_ID") {
        Toast.makeText(context, googleClientIdMissingLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }

    val googleSignInClient = GoogleSignIn.getClient(activity, createGoogleSignInOptions(webClientId))
    googleSignInClient.silentSignIn()
        .addOnSuccessListener { account ->
            if (account.idToken.isNullOrBlank()) {
                Toast.makeText(context, deleteAccountFailedLabel, Toast.LENGTH_LONG).show()
                onDeleteFinished()
                return@addOnSuccessListener
            }
            reauthenticateAndDeleteFirebaseAccount(
                context = context,
                deleteTargetUid = deleteTargetUid,
                account = account,
                deleteAccountSuccessLabel = deleteAccountSuccessLabel,
                deleteAccountFailedLabel = deleteAccountFailedLabel,
                deleteAccountMismatchLabel = deleteAccountMismatchLabel,
                onDeleteSuccess = onDeleteSuccess,
                onDeleteFinished = onDeleteFinished
            )
        }
        .addOnFailureListener {
            Toast.makeText(context, deleteAccountReauthRequiredLabel, Toast.LENGTH_LONG).show()
            onDeleteFinished()
        }
}

private fun startGithubReauthentication(
    activity: Activity,
    context: Context,
    deleteTargetUid: String,
    deleteAccountFailedLabel: String,
    deleteAccountSuccessLabel: String,
    deleteAccountMismatchLabel: String,
    onDeleteSuccess: () -> Unit,
    onDeleteFinished: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
        Toast.makeText(context, deleteAccountFailedLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }
    val provider = OAuthProvider.newBuilder(GITHUB_PROVIDER_ID).build()
    currentUser.startActivityForReauthenticateWithProvider(activity, provider)
        .addOnSuccessListener {
            val refreshedUser = FirebaseAuth.getInstance().currentUser
            if (refreshedUser == null || refreshedUser.uid != deleteTargetUid) {
                Toast.makeText(context, deleteAccountMismatchLabel, Toast.LENGTH_LONG).show()
                onDeleteFinished()
                return@addOnSuccessListener
            }
            refreshedUser.delete()
                .addOnSuccessListener {
                    Toast.makeText(context, deleteAccountSuccessLabel, Toast.LENGTH_SHORT).show()
                    onDeleteSuccess()
                    onDeleteFinished()
                }
                .addOnFailureListener { error ->
                    val message = error.localizedMessage ?: deleteAccountFailedLabel
                    Toast.makeText(context, "$deleteAccountFailedLabel: $message", Toast.LENGTH_LONG).show()
                    onDeleteFinished()
                }
        }
        .addOnFailureListener { error ->
            val message = error.localizedMessage ?: deleteAccountFailedLabel
            Toast.makeText(context, "$deleteAccountFailedLabel: $message", Toast.LENGTH_LONG).show()
            onDeleteFinished()
        }
}

private fun logoutFromFirebaseAndProviders(
    context: Context,
    logoutSuccessLabel: String,
    logoutFailedLabel: String,
    onLogoutSuccess: () -> Unit,
    onLogoutFailure: () -> Unit
) {
    FirebaseAuth.getInstance().signOut()

    val webClientId = resolveWebClientId(context)
    if (webClientId.isBlank() || webClientId == "YOUR_FIREBASE_WEB_CLIENT_ID") {
        Toast.makeText(context, logoutSuccessLabel, Toast.LENGTH_SHORT).show()
        onLogoutSuccess()
        return
    }
    val googleSignInClient = GoogleSignIn.getClient(context, createGoogleSignInOptions(webClientId))
    googleSignInClient.signOut()
        .addOnSuccessListener {
            Toast.makeText(context, logoutSuccessLabel, Toast.LENGTH_SHORT).show()
            onLogoutSuccess()
        }
        .addOnFailureListener {
            Toast.makeText(context, logoutFailedLabel, Toast.LENGTH_LONG).show()
            onLogoutFailure()
        }
}

private fun FirebaseUser.hasGithubProvider(): Boolean {
    return providerData.any { userInfo -> userInfo.providerId == GITHUB_PROVIDER_ID }
}

private fun reauthenticateAndDeleteFirebaseAccount(
    context: Context,
    deleteTargetUid: String,
    account: GoogleSignInAccount,
    deleteAccountSuccessLabel: String,
    deleteAccountFailedLabel: String,
    deleteAccountMismatchLabel: String,
    onDeleteSuccess: () -> Unit,
    onDeleteFinished: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
        Toast.makeText(context, deleteAccountFailedLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }
    if (currentUser.uid != deleteTargetUid) {
        Toast.makeText(context, deleteAccountMismatchLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }
    val currentEmail = currentUser.email
    val googleEmail = account.email
    if (
        !currentEmail.isNullOrBlank() &&
        !googleEmail.isNullOrBlank() &&
        currentEmail != googleEmail
    ) {
        Toast.makeText(context, deleteAccountMismatchLabel, Toast.LENGTH_LONG).show()
        onDeleteFinished()
        return
    }

    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
    currentUser.reauthenticate(credential)
        .addOnSuccessListener {
            val refreshedUser = FirebaseAuth.getInstance().currentUser
            if (refreshedUser == null || refreshedUser.uid != deleteTargetUid) {
                Toast.makeText(context, deleteAccountMismatchLabel, Toast.LENGTH_LONG).show()
                onDeleteFinished()
                return@addOnSuccessListener
            }
            refreshedUser.delete()
                .addOnSuccessListener {
                    signOutFromGoogle(
                        context = context,
                        onSuccess = {
                            Toast.makeText(context, deleteAccountSuccessLabel, Toast.LENGTH_SHORT).show()
                            onDeleteSuccess()
                            onDeleteFinished()
                        },
                        onFailure = {
                            Toast.makeText(context, deleteAccountSuccessLabel, Toast.LENGTH_SHORT).show()
                            onDeleteSuccess()
                            onDeleteFinished()
                        }
                    )
                }
                .addOnFailureListener { error ->
                    val message = error.localizedMessage ?: deleteAccountFailedLabel
                    Toast.makeText(context, "$deleteAccountFailedLabel: $message", Toast.LENGTH_LONG).show()
                    onDeleteFinished()
                }
        }
        .addOnFailureListener { error ->
            val message = error.localizedMessage ?: deleteAccountFailedLabel
            Toast.makeText(context, "$deleteAccountFailedLabel: $message", Toast.LENGTH_LONG).show()
            onDeleteFinished()
        }
}

private fun signOutFromGoogle(
    context: Context,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val webClientId = resolveWebClientId(context)
    GoogleSignIn.getClient(context, createGoogleSignInOptions(webClientId))
        .signOut()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure() }
}

private fun createGoogleSignInOptions(webClientId: String): GoogleSignInOptions {
    return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(/* serverClientId = */ webClientId)
        .requestEmail()
        .build()
}

private fun googleSignInFailureMessage(
    apiException: ApiException?,
    resultCode: Int,
    loginCanceledLabel: String,
    loginFailedLabel: String,
    googleSignInDeveloperErrorLabel: String,
    googleSignInInProgressLabel: String
): String {
    val statusCode = apiException?.statusCode
    return when (statusCode) {
        GoogleSignInStatusCodes.SIGN_IN_CANCELLED,
        CommonStatusCodes.CANCELED -> loginCanceledLabel
        CommonStatusCodes.DEVELOPER_ERROR -> "$loginFailedLabel: $googleSignInDeveloperErrorLabel ($statusCode)"
        GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "$loginFailedLabel: $googleSignInInProgressLabel ($statusCode)"
        null -> {
            if (resultCode == Activity.RESULT_CANCELED) {
                loginCanceledLabel
            } else {
                "$loginFailedLabel: resultCode=$resultCode"
            }
        }
        else -> {
            val statusMessage = apiException.status.statusMessage ?: "statusCode=$statusCode"
            "$loginFailedLabel: $statusMessage (${apiException::class.java.simpleName}, statusCode=$statusCode)"
        }
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
    firebaseNotConfiguredLabel: String,
    onLoginSuccess: () -> Unit,
    onLoginFailure: (String) -> Unit
) {
    Firebase.initialize(context)
    val auth = runCatching { FirebaseAuth.getInstance() }
        .getOrElse { error ->
            val message = authFailureMessage(firebaseNotConfiguredLabel, error)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onLoginFailure(message)
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
            onLoginSuccess()
        }
        .addOnFailureListener { error ->
            Log.w(TAG, "Firebase sign-in failed", error)
            val message = authFailureMessage(loginFailedLabel, error)
            Toast.makeText(context, "$loginFailedLabel: $message", Toast.LENGTH_LONG).show()
            onLoginFailure("$loginFailedLabel: $message")
        }
}

private fun authFailureMessage(
    fallbackMessage: String,
    error: Throwable
): String {
    val parts = buildList {
        add(error::class.java.simpleName)
        if (error is FirebaseAuthException) {
            add(error.errorCode)
        } else if (error is FirebaseException) {
            error.message?.substringBefore(':')?.takeIf { it.isNotBlank() }?.let(::add)
        }
    }
    val detail = error.localizedMessage ?: error.message ?: fallbackMessage
    return "${parts.joinToString(", ")}: $detail"
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
        ),
        listState = rememberLazyListState()
    )
}
