package com.hotel.evergreenkuakata.domain.user

import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.data.model.user.UserInfo

interface BookingRepo {
    suspend fun getAllBookings(): Result<List<BookingInfo>>
    suspend fun bookRoom(booking: BookingInfo): Result<Unit>
    suspend fun getBookingsForDate(date: String): Result<List<BookingInfo>>
    suspend fun deleteBooking(bookingId: String): Result<Unit>
    suspend fun updateBooking(booking: BookingInfo): Result<Unit>
    suspend fun getAllUsers(): Result<List<UserInfo>>
}
