package com.hotel.evergreenkuakata.presentation.room

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.databinding.ActivityRoomBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomActivity : BaseActivity<ActivityRoomBinding>() {
    private lateinit var activityBinding: ActivityRoomBinding

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initListeners()
    }

    override fun setVariables(dataBinding: ActivityRoomBinding) {
        activityBinding = dataBinding
    }

    private fun initListeners(){
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            finish()
        }
    }
}