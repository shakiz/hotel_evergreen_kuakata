package com.hotel.evergreenkuakata

import android.app.Application
import com.google.firebase.FirebaseApp
import com.hotel.evergreenkuakata.utils.LocaleManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        val language = LocaleManager.getCurrentLocale(this)
//        LocaleManager.setLocale(this, language)
        FirebaseApp.initializeApp(this)
    }
}