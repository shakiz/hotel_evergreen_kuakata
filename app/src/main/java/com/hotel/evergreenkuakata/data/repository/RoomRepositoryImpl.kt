package com.hotel.evergreenkuakata.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.domain.user.RoomRepo
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume


class RoomRepositoryImpl @Inject constructor(
    database: FirebaseDatabase,
) : RoomRepo {

    private val roomsRef = database.getReference("rooms")
    private val bookingsRef = database.getReference("bookings")

    override suspend fun addRoom(room: Room): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            try {
                val newRoomRef = roomsRef.push()
                val bookingWithId = room.copy(roomId = newRoomRef.key.orEmpty())
                newRoomRef.setValue(bookingWithId)
                    .addOnSuccessListener {
                        cont.resume(Result.success(Unit))
                    }
                    .addOnFailureListener {
                        cont.resume(Result.failure(it))
                    }

            } catch (e: Exception) {
                cont.resume(Result.failure(e))
            }
        }

    override suspend fun getRoomsWithAvailability(date: String): Result<List<RoomWithStatus>> =
        suspendCancellableCoroutine { cont ->
            roomsRef.get().addOnSuccessListener { roomSnapshot ->
                val rooms = roomSnapshot.children.mapNotNull { snap ->
                    snap.getValue(Room::class.java)?.copy(roomId = snap.key.orEmpty())
                }

                bookingsRef.get().addOnSuccessListener { bookingSnapshot ->
                    val bookedRoomIds = bookingSnapshot.children.mapNotNull { snap ->
                        val roomId = snap.child("roomId").getValue<String>()
                        val bookingDate = snap.child("bookingDate").getValue<String>()
                        val bookingStatus = snap.child("bookingStatus").getValue<String>()
                        if (bookingDate == date && bookingStatus != "cancelled" && bookingStatus != "checked_out") roomId else null
                    }

                    val result = rooms
                        .filter { it.roomId !in bookedRoomIds }
                        .map { RoomWithStatus(it, true) }
                    cont.resume(Result.success(result))
                }.addOnFailureListener {
                    cont.resume(Result.failure(it))
                }
            }.addOnFailureListener {
                cont.resume(Result.failure(it))
            }
        }


    override suspend fun getAllRooms(): Result<List<Room>> = suspendCancellableCoroutine { cont ->
        roomsRef.get().addOnSuccessListener { snapshot ->
            val rooms = snapshot.children.mapNotNull { snap ->
                snap.getValue(Room::class.java)?.copy(roomId = snap.key.orEmpty())
            }
            cont.resume(Result.success(rooms))
        }.addOnFailureListener {
            cont.resume(Result.failure(it))
        }
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            roomsRef.child(roomId).removeValue()
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    override suspend fun updateRoom(room: Room): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            roomsRef.child(room.roomId).setValue(room)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }
}
