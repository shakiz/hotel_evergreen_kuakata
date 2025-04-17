package com.hotel.evergreenkuakata.data.model.booking

import com.hotel.evergreenkuakata.data.model.room.Room

data class BookingInfo(
    val roomId: String = "",
    val customerName: String = "",
    val nid: String = "",
    val phone: String = "",
    val date: String = ""
)

data class RoomWithStatus(
    val room: Room,
    val isAvailable: Boolean
)

