package com.hotel.evergreenkuakata.presentation.booking

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.databinding.ActivityBookingListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingListActivity : BaseActivity<ActivityBookingListBinding>() {
    private lateinit var activityBinding: ActivityBookingListBinding

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_booking_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initListeners()
    }

    override fun setVariables(dataBinding: ActivityBookingListBinding) {
        activityBinding = dataBinding
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            finish()
        }
    }
}