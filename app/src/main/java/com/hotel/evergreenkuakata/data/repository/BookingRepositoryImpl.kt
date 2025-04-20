package com.hotel.evergreenkuakata.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.domain.user.BookingRepo
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class BookingRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : BookingRepo {

    private val bookingsRef = database.getReference("bookings")

    override suspend fun bookRoom(booking: BookingInfo): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val newRef = bookingsRef.push()
            val bookingWithId = booking.copy(bookingId = newRef.key ?: "")
            newRef.setValue(bookingWithId)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    override suspend fun getBookingsForDate(date: String): Result<List<BookingInfo>> =
        suspendCancellableCoroutine { cont ->
            bookingsRef.get()
                .addOnSuccessListener { snapshot ->
                    val bookings = snapshot.children.mapNotNull { snap ->
                        val booking = snap.getValue(BookingInfo::class.java)
                        if (booking?.checkInDate == date) {
                            booking.copy(bookingId = snap.key ?: "")
                        } else null
                    }
                    cont.resume(Result.success(bookings))
                }
                .addOnFailureListener {
                    cont.resume(Result.failure(it))
                }
        }

    override suspend fun deleteBooking(bookingId: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            bookingsRef.child(bookingId).removeValue()
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    override suspend fun updateBooking(booking: BookingInfo): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            bookingsRef.child(booking.bookingId).setValue(booking)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }
}
