package com.hotel.evergreenkuakata.di

import android.content.Context
import com.hotel.evergreenkuakata.data.remote.webservice.UserService
import com.hotel.evergreenkuakata.utils.ApiConstants
import com.hotel.evergreenkuakata.utils.PrefManager
import com.hotel.evergreenkuakata.utils.UtilsForAll
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePrefManager(@ApplicationContext context: Context): PrefManager {
        return PrefManager(context)
    }

    @Provides
    @Singleton
    fun provideUtils(@ApplicationContext context: Context): UtilsForAll {
        return UtilsForAll(context)
    }

    @Provides
    @Singleton
    fun provideUserService(
        callFactory: okhttp3.Call.Factory
    ): UserService = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .callFactory(callFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserService::class.java)
}
