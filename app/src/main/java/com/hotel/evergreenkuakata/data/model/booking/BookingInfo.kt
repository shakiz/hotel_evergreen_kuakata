package com.hotel.evergreenkuakata.data.model.booking

import com.hotel.evergreenkuakata.data.model.room.Room

data class BookingInfo(
    var bookingId: String = "",
    var roomId: String = "",
    var customerName: String = "",
    var customerNid: String = "",
    var phone: String = "",
    var checkInDate: String = "",   // Format: yyyy-MM-dd
    var checkOutDate: String = "",  // Format: yyyy-MM-dd
    var bookingStatus: BookingStatus = BookingStatus.ACTIVE,
    var totalAmount: Int = 0,
    var createdAt: Long = System.currentTimeMillis()
)

enum class BookingStatus {
    ACTIVE,        // Booking confirmed, not yet checked in
    CHECKED_IN,    // Guest has checked in
    COMPLETED,     // Guest has checked out
    CANCELLED      // Booking was cancelled
}

data class RoomWithStatus(
    var room: Room,
    var isAvailable: Boolean
)

