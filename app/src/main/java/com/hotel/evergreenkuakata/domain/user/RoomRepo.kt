package com.hotel.evergreenkuakata.domain.user

import android.net.Uri
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.room.Room

interface RoomRepo {
    suspend fun addRoom(room: Room, imageUri: Uri): Result<Unit>
    suspend fun getRoomsWithAvailability(date: String): Result<List<RoomWithStatus>>
    suspend fun getAllRooms(): Result<List<Room>>
    suspend fun deleteRoom(roomId: String): Result<Unit>
    suspend fun updateRoom(room: Room): Result<Unit>
}
