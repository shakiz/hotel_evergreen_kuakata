package com.hotel.evergreenkuakata.data.model.room

data class Room(
    var roomId: String = "",
    val imageUrl: String = "",
    val name: String = "",
    val price: Int = 0,
    val roomType: String = ""
)