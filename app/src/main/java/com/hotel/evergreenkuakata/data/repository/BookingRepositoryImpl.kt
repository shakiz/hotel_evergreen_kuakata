package com.hotel.evergreenkuakata.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.data.model.user.UserInfo
import com.hotel.evergreenkuakata.domain.user.BookingRepo
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class BookingRepositoryImpl @Inject constructor(
    database: FirebaseDatabase
) : BookingRepo {

    private val bookingsRef = database.getReference("bookings")
    private val usersRef = database.getReference("users")

    override suspend fun getAllBookings(): Result<List<BookingInfo>> =
        suspendCancellableCoroutine { cont ->
            bookingsRef.get().addOnSuccessListener { snapshot ->
                val rooms = snapshot.children.mapNotNull { snap ->
                    snap.getValue(BookingInfo::class.java)?.copy(roomId = snap.key.orEmpty())
                }
                cont.resume(Result.success(rooms))
            }.addOnFailureListener {
                cont.resume(Result.failure(it))
            }
        }

    override suspend fun bookRoom(booking: BookingInfo): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val newRef = bookingsRef.push()
            val bookingWithId = booking.copy(bookingId = newRef.key.orEmpty())
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
                        if (booking?.bookingDate == date) {
                            booking.copy(bookingId = snap.key.orEmpty())
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

    override suspend fun getAllUsers(): Result<List<UserInfo>> =
        suspendCancellableCoroutine { cont ->
            usersRef.get().addOnSuccessListener { snapshot ->
                val users = snapshot.children.mapNotNull { snap ->
                    snap.getValue(UserInfo::class.java)
                }
                cont.resume(Result.success(users))
            }.addOnFailureListener {
                cont.resume(Result.failure(it))
            }
        }
}
