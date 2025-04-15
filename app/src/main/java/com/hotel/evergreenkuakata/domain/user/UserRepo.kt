package com.hotel.evergreenkuakata.domain.user

import com.hotel.evergreenkuakata.data.model.BaseApiResponse
import com.hotel.evergreenkuakata.data.model.user.UserInfo
import com.hotel.evergreenkuakata.utils.Resource

interface UserRepo {
    suspend fun getProfile(token: String): Resource<UserInfo>
    suspend fun updateProfile(userInfo: UserInfo, token: String): Resource<BaseApiResponse>
}
