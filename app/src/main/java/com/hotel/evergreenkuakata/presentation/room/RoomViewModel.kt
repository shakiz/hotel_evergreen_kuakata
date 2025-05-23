package com.hotel.evergreenkuakata.presentation.room

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

    private val _loading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _loading

    private val _roomsWithAvailability = MutableStateFlow<List<RoomWithStatus>>(emptyList())
    val roomsWithAvailability: StateFlow<List<RoomWithStatus>> = _roomsWithAvailability

    private val _allRooms = MutableStateFlow<List<Room>>(emptyList())
    val allRooms: StateFlow<List<Room>> = _allRooms

    private val _addRoomStatus = MutableStateFlow<Result<Unit>?>(null)
    val addRoomStatus: StateFlow<Result<Unit>?> = _addRoomStatus

    private val _updateRoomStatus = MutableStateFlow<Result<Unit>?>(null)
    val updateRoomStatus: StateFlow<Result<Unit>?> = _updateRoomStatus

    private val _deleteRoomStatus = MutableStateFlow<Result<Unit>?>(null)
    val deleteRoomStatus: StateFlow<Result<Unit>?> = _deleteRoomStatus

    fun fetchRoomsWithAvailability(date: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = roomRepository.getRoomsWithAvailability(date)
            result.onSuccess {
                _roomsWithAvailability.value = it
                _loading.value = false
            }.onFailure {
                _loading.value = false
                _roomsWithAvailability.value = emptyList() // or handle error appropriately
            }
        }
    }

    fun fetchAllRooms() {
        viewModelScope.launch {
            _loading.value = true
            val result = roomRepository.getAllRooms()
            result.onSuccess {
                _allRooms.value = it
                _loading.value = false
            }.onFailure {
                _loading.value = false
                _allRooms.value = emptyList()
            }
        }
    }

    fun addRoom(room: Room) {
        viewModelScope.launch {
            _loading.value = true
            val result = roomRepository.addRoom(room)
            _addRoomStatus.value = result
            _loading.value = false
        }
    }

    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = roomRepository.deleteRoom(roomId)
            _deleteRoomStatus.value = result
            _loading.value = false
            fetchAllRooms()
        }
    }

    fun updateRoom(room: Room) {
        viewModelScope.launch {
            _loading.value = true
            val result = roomRepository.updateRoom(room)
            _updateRoomStatus.value = result
            _loading.value = false
        }
    }
}
