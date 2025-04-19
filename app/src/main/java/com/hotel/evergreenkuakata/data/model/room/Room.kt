package com.hotel.evergreenkuakata.data.model.room

data class Room(
    var roomId: String = "",
    var imageUrl: String = "",
    var name: String = "",
    var pricePerNight: Int = 0,
    var roomCategoryId: Int? = null,
    var createdAt: Long = System.currentTimeMillis()
)

data class RoomCategory(
    val roomCategoryId: Int,
    val roomCategoryName: String
)