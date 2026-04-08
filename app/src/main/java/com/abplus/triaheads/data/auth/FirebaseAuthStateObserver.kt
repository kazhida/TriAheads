package com.abplus.triaheads.data.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class FirebaseAuthStateObserver @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthStateObserver {
    private val _isLoggedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _isLoggedIn.value = auth.currentUser != null
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }
}
