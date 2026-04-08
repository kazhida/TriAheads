package com.abplus.triaheads.data.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthStateObserver {
    val isLoggedIn: StateFlow<Boolean>
}
