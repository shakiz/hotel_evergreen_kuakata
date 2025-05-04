package com.hotel.evergreenkuakata.presentation.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.data.repository.AuthRepositoryImpl
import com.hotel.evergreenkuakata.data.repository.BookingRepositoryImpl
import com.hotel.evergreenkuakata.data.repository.RoomRepositoryImpl
import com.hotel.evergreenkuakata.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepoImpl: AuthRepositoryImpl,
    private val bookingRepositoryImpl: BookingRepositoryImpl,
    private val roomRepositoryImpl: RoomRepositoryImpl,
) : ViewModel() {

    private val _allRooms = MutableStateFlow<List<Room>>(emptyList())
    val allRooms: StateFlow<List<Room>> = _allRooms

    private val _roomsWithAvailability = MutableStateFlow<List<RoomWithStatus>>(emptyList())
    val roomsWithAvailability: StateFlow<List<RoomWithStatus>> = _roomsWithAvailability

    private val _allBookings = MutableStateFlow<List<BookingInfo>>(emptyList())
    val allBookings: StateFlow<List<BookingInfo>> = _allBookings

    private val _bookingsForDate = MutableStateFlow<List<BookingInfo>>(emptyList())
    val bookingsForDate: StateFlow<List<BookingInfo>> = _bookingsForDate

    fun fetchRoomsWithAvailability(date: String) {
        viewModelScope.launch {
            val result = roomRepositoryImpl.getRoomsWithAvailability(date)
            result.onSuccess {
                _roomsWithAvailability.value = it
            }.onFailure {
                _roomsWithAvailability.value = emptyList() // or handle error appropriately
            }
        }
    }

    fun fetchAllBookings() {
        viewModelScope.launch {
            val result = bookingRepositoryImpl.getAllBookings()
            result.onSuccess {
                _allBookings.value = it
            }.onFailure {
                _allBookings.value = emptyList()
            }
        }
    }

    fun fetchBookingsForDate(date: String) {
        viewModelScope.launch {
            val result = bookingRepositoryImpl.getBookingsForDate(date)
            result.onSuccess {
                _bookingsForDate.value = it
            }.onFailure {
                _bookingsForDate.value = emptyList()
            }
        }
    }

    fun logout() {
        authRepoImpl.logoutUser()
    }
}