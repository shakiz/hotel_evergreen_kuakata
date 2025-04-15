package com.hotel.evergreenkuakata.data.model.user

import com.google.gson.annotations.SerializedName
import com.hotel.evergreenkuakata.data.model.BaseApiResponse

data class BaseUserResponse(
    @SerializedName("data") var data: UserInfo? = null
) : BaseApiResponse()
