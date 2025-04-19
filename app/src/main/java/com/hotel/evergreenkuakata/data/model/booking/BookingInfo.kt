package com.hotel.evergreenkuakata.data.model.booking

import com.hotel.evergreenkuakata.data.model.room.Room

data class BookingInfo(
    val bookingId: String = "",
    val roomId: String = "",
    val customerName: String = "",
    val nid: String = "",
    val phone: String = "",
    val checkInDate: String = "",   // Format: yyyy-MM-dd
    val checkOutDate: String = "",  // Format: yyyy-MM-dd
    val bookingStatus: BookingStatus = BookingStatus.ACTIVE,
    val totalAmount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BookingStatus {
    ACTIVE,        // Booking confirmed, not yet checked in
    CHECKED_IN,    // Guest has checked in
    COMPLETED,     // Guest has checked out
    CANCELLED      // Booking was cancelled
}

data class RoomWithStatus(
    val room: Room,
    val isAvailable: Boolean
)

