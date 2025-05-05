package com.hotel.evergreenkuakata.presentation.booking

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.databinding.ActivityBookingListBinding
import com.hotel.evergreenkuakata.presentation.adapter.BookingAdapter
import com.hotel.evergreenkuakata.utils.SpinnerData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookingListActivity : BaseActivity<ActivityBookingListBinding>(), BookingAdapter.BookingCallbacks {
    private lateinit var activityBinding: ActivityBookingListBinding
    private lateinit var bookingAdapter: BookingAdapter
    private val viewModel by viewModels<BookingViewModel>()
    private lateinit var spinnerData: SpinnerData

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
        initVariables()
        initListeners()
        setRecyclerAdapter()
        initObservers()
        viewModel.fetchAllBookings()
    }

    override fun setVariables(dataBinding: ActivityBookingListBinding) {
        activityBinding = dataBinding
    }

    private fun initVariables() {
        spinnerData = SpinnerData(this)
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.allBookings.collect { bookings ->
                bookingAdapter.setItems(bookings)
            }
        }
    }

    private fun setRecyclerAdapter() {
        bookingAdapter = BookingAdapter()
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = bookingAdapter
        bookingAdapter.setOnBookingClick(this)
    }

    override fun onItemClick(bookingInfo: BookingInfo) {
        startActivity(
            Intent(
                this@BookingListActivity,
                BookingActivity::class.java
            ).putExtra("bookingInfo", bookingInfo)
        )
    }
}