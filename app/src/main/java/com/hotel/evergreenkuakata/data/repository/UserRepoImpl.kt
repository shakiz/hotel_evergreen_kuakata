package com.hotel.evergreenkuakata.data.repository

import com.google.gson.Gson
import com.hotel.evergreenkuakata.data.model.BaseApiResponse
import com.hotel.evergreenkuakata.data.model.user.UserInfo
import com.hotel.evergreenkuakata.data.remote.webservice.UserService
import com.hotel.evergreenkuakata.domain.user.UserRepo
import com.hotel.evergreenkuakata.utils.Constants.ACCEPT
import com.hotel.evergreenkuakata.utils.Constants.CONTENT_TYPE
import com.hotel.evergreenkuakata.utils.ErrorType
import com.hotel.evergreenkuakata.utils.Resource
import java.net.SocketTimeoutException
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val userService: UserService
) : UserRepo {
    override suspend fun getProfile(token: String): Resource<UserInfo> {
        try {
            val task = userService.getProfile(
                token = "Bearer $token",
                accept = ACCEPT,
                contentType = CONTENT_TYPE,
            )
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it.data)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.errorBody() != null) {
                val errorBodyStr = task.errorBody()?.string()
                val baseApiResponse: BaseApiResponse =
                    Gson().fromJson(errorBodyStr, BaseApiResponse::class.java)
                return if (baseApiResponse.statusCode == 500) {
                    Resource.Error(
                        errorType = ErrorType.INTERNAL_SERVER_ERROR,
                        message = baseApiResponse.message
                    )
                } else {
                    Resource.Error(
                        errorType = ErrorType.UNKNOWN,
                        message = baseApiResponse.message
                    )
                }
            } else {
                return Resource.Error(errorType = ErrorType.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }

    override suspend fun updateProfile(
        userInfo: UserInfo,
        token: String
    ): Resource<BaseApiResponse> {
        try {
            val task = userService.updateProfile(
                token = "Bearer $token",
                accept = ACCEPT,
                contentType = CONTENT_TYPE,
                userInfo = userInfo
            )
            if (task.isSuccessful) {
                task.body()?.let {
                    return Resource.Success(response = it)
                } ?: return Resource.Error(errorType = ErrorType.EMPTY_DATA)
            } else if (task.errorBody() != null) {
                val errorBodyStr = task.errorBody()?.string()
                val baseApiResponse: BaseApiResponse =
                    Gson().fromJson(errorBodyStr, BaseApiResponse::class.java)
                return if (baseApiResponse.statusCode == 500) {
                    Resource.Error(
                        errorType = ErrorType.INTERNAL_SERVER_ERROR,
                        message = baseApiResponse.message
                    )
                } else {
                    Resource.Error(
                        errorType = ErrorType.UNKNOWN,
                        message = baseApiResponse.message
                    )
                }
            } else {
                return Resource.Error(errorType = ErrorType.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            return Resource.Error(errorType = ErrorType.TIME_OUT)
        } catch (e: Exception) {
            return Resource.Error(message = e.localizedMessage ?: "")
        }
    }
}
