package com.hotel.evergreenkuakata.data.model.booking

import android.os.Parcel
import android.os.Parcelable
import com.hotel.evergreenkuakata.data.model.room.Room

data class BookingInfo(
    var bookingId: String = "",
    var roomId: String = "",
    var roomName: String = "",
    var customerName: String = "",
    var customerNid: String = "",
    var phone: String = "",
    var checkInDate: String = "",
    var checkOutDate: String = "",
    var bookingDate: String = "",
    var bookingStatus: String = "",
    var pricePerNight: Int = 0,
    var bookingAdvance: Int = 0,
    var createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookingId)
        parcel.writeString(roomId)
        parcel.writeString(roomName)
        parcel.writeString(customerName)
        parcel.writeString(customerNid)
        parcel.writeString(phone)
        parcel.writeString(checkInDate)
        parcel.writeString(checkOutDate)
        parcel.writeString(bookingDate)
        parcel.writeString(bookingStatus)
        parcel.writeInt(pricePerNight)
        parcel.writeInt(bookingAdvance)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingInfo> {
        override fun createFromParcel(parcel: Parcel): BookingInfo {
            return BookingInfo(parcel)
        }

        override fun newArray(size: Int): Array<BookingInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class RoomWithStatus(
    var room: Room,
    var isAvailable: Boolean
)

