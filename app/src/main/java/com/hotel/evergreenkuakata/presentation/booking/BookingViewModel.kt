package com.hotel.evergreenkuakata.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.user.UserInfo
import com.hotel.evergreenkuakata.data.repository.BookingRepositoryImpl
import com.hotel.evergreenkuakata.data.repository.RoomRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepositoryImpl,
    private val roomRepository: RoomRepositoryImpl
) : ViewModel() {

    private val _loading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _loading

    private val _allBookings = MutableStateFlow<List<BookingInfo>>(emptyList())
    val allBookings: StateFlow<List<BookingInfo>> = _allBookings

    private val _bookingStatus = MutableStateFlow<Result<Unit>?>(null)
    val bookingStatus: StateFlow<Result<Unit>?> = _bookingStatus

    private val _bookingsForDate = MutableStateFlow<List<BookingInfo>>(emptyList())
    val bookingsForDate: StateFlow<List<BookingInfo>> = _bookingsForDate

    private val _allUsers = MutableStateFlow<List<UserInfo>>(emptyList())
    val allUsers: StateFlow<List<UserInfo>> = _allUsers

    private val _roomsWithAvailability = MutableStateFlow<List<RoomWithStatus>>(emptyList())
    val roomsWithAvailability: StateFlow<List<RoomWithStatus>> = _roomsWithAvailability

    private val _updateBookingStatus = MutableStateFlow<Result<Unit>?>(null)
    val updateBookingStatus: StateFlow<Result<Unit>?> = _updateBookingStatus

    fun fetchAllBookings() {
        viewModelScope.launch {
            _loading.value = true
            val result = bookingRepository.getAllBookings()
            result.onSuccess {
                _allBookings.value = it
                _loading.value = false
            }.onFailure {
                _loading.value = false
                _allBookings.value = emptyList()
            }
        }
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _loading.value = true
            val result = bookingRepository.getAllUsers()
            result.onSuccess {
                _allUsers.value = it
                _loading.value = false
            }.onFailure {
                _loading.value = false
                _allUsers.value = emptyList()
            }
        }
    }

    fun fetchRoomsWithAvailability(date: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = roomRepository.getRoomsWithAvailability(date)
            result.onSuccess {
                _loading.value = false
                _roomsWithAvailability.value = it
            }.onFailure {
                _loading.value = false
                _roomsWithAvailability.value = emptyList() // or handle error appropriately
            }
        }
    }

    fun bookRoom(booking: BookingInfo) {
        viewModelScope.launch {
            _loading.value = true
            val result = bookingRepository.bookRoom(booking)
            _bookingStatus.value = result
            _loading.value = false
        }
    }

    fun fetchBookingsForDate(date: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = bookingRepository.getBookingsForDate(date)
            result.onSuccess {
                _bookingsForDate.value = it
                _loading.value = false
            }.onFailure {
                _loading.value = false
                _bookingsForDate.value = emptyList()
            }
        }
    }

    fun updateBooking(booking: BookingInfo) {
        viewModelScope.launch {
            _loading.value = true
            val result = bookingRepository.updateBooking(booking)
            _updateBookingStatus.value = result
            _loading.value = false
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            _loading.value = true
            bookingRepository.deleteBooking(bookingId)
            fetchAllBookings()
            _loading.value = false
        }
    }

    fun getTotalAmountByRoomId(roomId: String): Int{
        val matchedAvailableRoom = roomsWithAvailability.value.first { it.room.roomId == roomId }
        return matchedAvailableRoom.room.pricePerNight
    }
}
