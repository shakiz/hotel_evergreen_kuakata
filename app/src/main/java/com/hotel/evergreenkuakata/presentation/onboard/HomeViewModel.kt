package com.hotel.evergreenkuakata.presentation.onboard

import androidx.lifecycle.ViewModel
import com.hotel.evergreenkuakata.data.repository.AuthRepositoryImpl
import com.hotel.evergreenkuakata.data.repository.BookingRepositoryImpl
import com.hotel.evergreenkuakata.data.repository.RoomRepositoryImpl
import com.hotel.evergreenkuakata.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepoImpl: AuthRepositoryImpl,
    private val bookingRepositoryImpl: BookingRepositoryImpl,
    private val roomRepositoryImpl: RoomRepositoryImpl,
) : ViewModel() {

    fun logout() {
        authRepoImpl.logoutUser()
    }
}