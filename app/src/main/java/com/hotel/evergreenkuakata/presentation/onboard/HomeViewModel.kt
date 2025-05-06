package com.hotel.evergreenkuakata.presentation.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.data.model.booking.IncomeInfo
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.data.repository.AuthRepositoryImpl
import com.hotel.evergreenkuakata.data.repository.BookingRepositoryImpl
import com.hotel.evergreenkuakata.data.repository.RoomRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
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

    private val _incomeData = MutableStateFlow(IncomeInfo(0, 0))
    val incomeData: StateFlow<IncomeInfo> = _incomeData

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

    fun fetchAllRooms() {
        viewModelScope.launch {
            val result = roomRepositoryImpl.getAllRooms()
            result.onSuccess {
                _allRooms.value = it
            }.onFailure {
                _allRooms.value = emptyList()
            }
        }
    }

    fun fetchAllBookings() {
        viewModelScope.launch {
            val result = bookingRepositoryImpl.getAllBookings()
            result.onSuccess {
                _allBookings.value = it

                val now = Calendar.getInstance()
                // Today's start and end timestamps
                val startOfToday = now.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val endOfToday = now.apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis

                // This month's start timestamp
                val startOfMonth = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val todayTotal = _allBookings.value
                    .filter { booking -> booking.createdAt in startOfToday..endOfToday }
                    .sumOf { booking -> booking.pricePerNight }

                val monthTotal = _allBookings.value
                    .filter { booking -> booking.createdAt >= startOfMonth }
                    .sumOf { booking -> booking.pricePerNight }
                _incomeData.value = IncomeInfo(thisMonth = monthTotal, today = todayTotal)
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