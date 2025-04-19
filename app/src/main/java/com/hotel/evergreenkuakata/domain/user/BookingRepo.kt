package com.hotel.evergreenkuakata.domain.user

import com.hotel.evergreenkuakata.data.model.booking.BookingInfo

interface BookingRepo {
    suspend fun bookRoom(booking: BookingInfo): Result<Unit>
    suspend fun getBookingsForDate(date: String): Result<List<BookingInfo>>
    suspend fun deleteBooking(bookingId: String): Result<Unit>
    suspend fun updateBooking(booking: BookingInfo): Result<Unit>
}
