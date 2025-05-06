package com.hotel.evergreenkuakata.data.model.user

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id") var userId: Int = 0,
    @SerializedName("name") var name: String = ""
)