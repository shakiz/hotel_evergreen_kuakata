package com.hotel.evergreenkuakata.presentation.room

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.data.repository.RoomRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepositoryImpl
) : ViewModel() {

    private val _roomsWithAvailability = MutableStateFlow<List<RoomWithStatus>>(emptyList())
    val roomsWithAvailability: StateFlow<List<RoomWithStatus>> = _roomsWithAvailability

    private val _allRooms = MutableStateFlow<List<Room>>(emptyList())
    val allRooms: StateFlow<List<Room>> = _allRooms

    private val _addRoomStatus = MutableStateFlow<Result<Unit>?>(null)
    val addRoomStatus: StateFlow<Result<Unit>?> = _addRoomStatus

    private val _updateRoomStatus = MutableStateFlow<Result<Unit>?>(null)
    val updateRoomStatus: StateFlow<Result<Unit>?> = _updateRoomStatus

    fun fetchRoomsWithAvailability(date: String) {
        viewModelScope.launch {
            val result = roomRepository.getRoomsWithAvailability(date)
            result.onSuccess {
                _roomsWithAvailability.value = it
            }.onFailure {
                _roomsWithAvailability.value = emptyList() // or handle error appropriately
            }
        }
    }

    fun fetchAllRooms() {
        viewModelScope.launch {
            val result = roomRepository.getAllRooms()
            result.onSuccess {
                _allRooms.value = it
            }.onFailure {
                _allRooms.value = emptyList()
            }
        }
    }

    fun addRoom(room: Room) {
        viewModelScope.launch {
            val result = roomRepository.addRoom(room)
            _addRoomStatus.value = result
        }
    }

    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            roomRepository.deleteRoom(roomId)
            fetchAllRooms() // refresh after deletion
        }
    }

    fun updateRoom(room: Room) {
        viewModelScope.launch {
            val result = roomRepository.updateRoom(room)
            _updateRoomStatus.value = result
        }
    }
}
