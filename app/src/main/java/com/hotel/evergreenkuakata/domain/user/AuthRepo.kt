package com.hotel.evergreenkuakata.domain.user

import com.google.firebase.auth.FirebaseUser

interface AuthRepo {
    suspend fun registerUser(email: String, password: String): Result<FirebaseUser?>
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser?>
    fun logoutUser()
    fun getCurrentUser(): FirebaseUser?
}
