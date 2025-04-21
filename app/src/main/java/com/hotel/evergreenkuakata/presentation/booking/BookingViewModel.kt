package com.hotel.evergreenkuakata.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.data.repository.BookingRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepositoryImpl
) : ViewModel() {

    private val _allBookings = MutableStateFlow<List<BookingInfo>>(emptyList())
    val allBookings: StateFlow<List<BookingInfo>> = _allBookings

    private val _bookingStatus = MutableStateFlow<Result<Unit>?>(null)
    val bookingStatus: StateFlow<Result<Unit>?> = _bookingStatus

    private val _bookingsForDate = MutableStateFlow<List<BookingInfo>>(emptyList())
    val bookingsForDate: StateFlow<List<BookingInfo>> = _bookingsForDate

    fun fetchAllBookings() {
        viewModelScope.launch {
            val result = bookingRepository.getAllBookings()
            result.onSuccess {
                _allBookings.value = it
            }.onFailure {
                _allBookings.value = emptyList()
            }
        }
    }

    fun bookRoom(booking: BookingInfo) {
        viewModelScope.launch {
            val result = bookingRepository.bookRoom(booking)
            _bookingStatus.value = result
        }
    }

    fun fetchBookingsForDate(date: String) {
        viewModelScope.launch {
            val result = bookingRepository.getBookingsForDate(date)
            result.onSuccess {
                _bookingsForDate.value = it
            }.onFailure {
                _bookingsForDate.value = emptyList()
            }
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.deleteBooking(bookingId)
            // Optionally refresh the bookings list
            // fetchBookingsForDate(...)
        }
    }
}
