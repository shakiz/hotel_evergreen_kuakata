package com.hotel.evergreenkuakata.presentation.onboard

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.utils.LocaleManager
import com.hotel.evergreenkuakata.utils.PrefManager
import com.hotel.evergreenkuakata.utils.Tools
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var tools = Tools(this)

    @Inject
    lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        bindUIWithComponents()
    }

    private fun bindUIWithComponents() {
        tools.checkLogin(prefManager)
    }

//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(LocaleManager.applyLocale(newBase))
//    }
}