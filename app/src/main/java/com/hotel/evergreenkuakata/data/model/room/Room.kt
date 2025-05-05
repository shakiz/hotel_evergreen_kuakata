package com.hotel.evergreenkuakata.data.model.room

import android.os.Parcel
import android.os.Parcelable

data class Room(
    var roomId: String = "",
    var imageUrl: String = "",
    var name: String = "",
    var pricePerNight: Int = 0,
    var roomCategoryId: Int = 0,
    var roomCategoryText: String = "",
    var createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().orEmpty(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roomId)
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeInt(pricePerNight)
        parcel.writeInt(roomCategoryId)
        parcel.writeString(roomCategoryText)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Room> {
        override fun createFromParcel(parcel: Parcel): Room {
            return Room(parcel)
        }

        override fun newArray(size: Int): Array<Room?> {
            return arrayOfNulls(size)
        }
    }
}

data class RoomCategory(
    val roomCategoryId: Int,
    val roomCategoryName: String
)