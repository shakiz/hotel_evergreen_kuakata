package com.hotel.evergreenkuakata.data.remote

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.data.model.booking.RoomWithStatus
import com.hotel.evergreenkuakata.data.model.room.Room
import java.util.UUID

object FirebaseDbHelper {
    private val db: FirebaseFirestore
        get() = Firebase.firestore

    private val storage: FirebaseStorage
        get() = Firebase.storage

    fun addRoom(room: Room, imageUri: Uri, onComplete: (Boolean) -> Unit) {
        val imageRef = storage.reference.child("room_images/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri).continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
            imageRef.downloadUrl
        }.addOnSuccessListener { uri ->
            val roomData = room.copy(imageUrl = uri.toString())
            db.collection("rooms").add(roomData)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun bookRoom(booking: BookingInfo, onComplete: (Boolean) -> Unit) {
        db.collection("bookings").add(booking)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getRoomsWithAvailability(date: String, callback: (List<RoomWithStatus>) -> Unit) {
        db.collection("rooms").get().addOnSuccessListener { roomsSnapshot ->
            val rooms = roomsSnapshot.documents.mapNotNull {
                it.toObject(Room::class.java)
                    ?.apply { roomId = it.id }
            }
            db.collection("bookings").whereEqualTo("date", date).get()
                .addOnSuccessListener { bookingsSnapshot ->
                    val bookedRoomIds =
                        bookingsSnapshot.documents.mapNotNull { it.getString("roomId") }
                    val result = rooms.map { RoomWithStatus(it, it.roomId !in bookedRoomIds) }
                    callback(result)
                }
        }
    }
}
