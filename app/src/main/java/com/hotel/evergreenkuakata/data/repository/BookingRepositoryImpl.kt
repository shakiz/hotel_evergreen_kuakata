package com.hotel.evergreenkuakata.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.domain.user.BookingRepo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BookingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BookingRepo {

    override suspend fun bookRoom(booking: BookingInfo): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("bookings").add(booking)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    override suspend fun getBookingsForDate(date: String): Result<List<BookingInfo>> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("bookings").whereEqualTo("date", date).get()
                .addOnSuccessListener { snapshot ->
                    val bookings = snapshot.documents.mapNotNull {
                        it.toObject(BookingInfo::class.java)?.copy(bookingId = it.id)
                    }
                    cont.resume(Result.success(bookings))
                }.addOnFailureListener {
                    cont.resume(Result.failure(it))
                }
        }

    override suspend fun deleteBooking(bookingId: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("bookings").document(bookingId)
                .delete()
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    override suspend fun updateBooking(booking: BookingInfo): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("bookings").document(booking.bookingId)
                .set(booking)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }
}

