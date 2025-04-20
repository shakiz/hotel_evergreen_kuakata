package com.hotel.evergreenkuakata.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hotel.evergreenkuakata.utils.PrefManager
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.UtilsForAll
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

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
    fun provideTools(@ApplicationContext context: Context): Tools {
        return Tools(context)
    }
}
