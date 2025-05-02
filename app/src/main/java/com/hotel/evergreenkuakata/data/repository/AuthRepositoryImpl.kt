package com.hotel.evergreenkuakata.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hotel.evergreenkuakata.domain.user.AuthRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepo {

    override suspend fun registerUser(email: String, password: String): Result<FirebaseUser?> =
        withContext(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .await()
                Result.success(authResult.user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun loginUser(email: String, password: String): Result<FirebaseUser?> =
        withContext(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .await()
                Result.success(authResult.user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun logoutUser() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
}
