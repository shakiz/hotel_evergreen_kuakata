package com.hotel.evergreenkuakata

import android.app.Application
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.hotel.evergreenkuakata.utils.LocaleManager
import com.hotel.evergreenkuakata.utils.PrefManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        lateinit var prefManager: PrefManager
    }

    override fun onCreate() {
        super.onCreate()
        val language = LocaleManager.getCurrentLocale(this)
        LocaleManager.setLocale(this, language)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
    }
}