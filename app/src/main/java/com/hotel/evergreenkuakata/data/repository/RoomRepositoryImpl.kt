package com.hotel.evergreenkuakata.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.domain.user.RoomRepo
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

class RoomRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : RoomRepo {

    override suspend fun addRoom(room: Room, imageUri: Uri): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val imageRef = storage.reference.child("room_images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri).continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                imageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                val roomData = room.copy(imageUrl = uri.toString())
                firestore.collection("rooms").add(roomData)
                    .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                    .addOnFailureListener { cont.resume(Result.failure(it)) }
            }.addOnFailureListener {
                cont.resume(Result.failure(it))
            }
        }

    override suspend fun getRoomsWithAvailability(date: String): Result<List<RoomWithStatus>> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("rooms").get().addOnSuccessListener { roomsSnapshot ->
                val rooms = roomsSnapshot.documents.mapNotNull {
                    it.toObject(Room::class.java)?.copy(roomId = it.id)
                }
                firestore.collection("bookings").whereEqualTo("date", date).get()
                    .addOnSuccessListener { bookingsSnapshot ->
                        val bookedIds =
                            bookingsSnapshot.documents.mapNotNull { it.getString("roomId") }
                        val result = rooms.map { RoomWithStatus(it, it.roomId !in bookedIds) }
                        cont.resume(Result.success(result))
                    }.addOnFailureListener {
                        cont.resume(Result.failure(it))
                    }
            }.addOnFailureListener {
                cont.resume(Result.failure(it))
            }
        }

    override suspend fun getAllRooms(): Result<List<Room>> = suspendCancellableCoroutine { cont ->
        firestore.collection("rooms").get().addOnSuccessListener { snapshot ->
            val rooms = snapshot.documents.mapNotNull {
                it.toObject(Room::class.java)?.copy(roomId = it.id)
            }
            cont.resume(Result.success(rooms))
        }.addOnFailureListener {
            cont.resume(Result.failure(it))
        }
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("rooms").document(roomId)
                .delete()
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }

    override suspend fun updateRoom(room: Room): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("rooms").document(room.roomId)
                .set(room)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { cont.resume(Result.failure(it)) }
        }
}
