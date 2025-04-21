package com.hotel.evergreenkuakata.presentation.booking

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.databinding.ActivityBookingBinding
import com.hotel.evergreenkuakata.utils.SpinnerAdapter
import com.hotel.evergreenkuakata.utils.SpinnerData
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookingActivity : BaseActivity<ActivityBookingBinding>() {
    private lateinit var activityBinding: ActivityBookingBinding
    private var booking = BookingInfo()
    private var command = "add"
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)

    @Inject
    lateinit var tools: Tools
    private val viewModel by viewModels<BookingViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_booking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadData()
        bindUiWithComponents()
        initListeners()
    }

    override fun setVariables(dataBinding: ActivityBookingBinding) {
        activityBinding = dataBinding
    }

    private fun loadData() {
        if (booking.bookingId.isNotEmpty()) {
            command = "update"
        }
    }

    private fun bindUiWithComponents() {
        spinnerAdapter.setSpinnerAdapter(
            activityBinding.roomId,
            this,
            spinnerData.setSpinnerNoData()
        )
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            finish()
        }

        activityBinding.btnBook.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    saveOrUpdateData()
                } else {
                    Toast.makeText(
                        this@BookingActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        activityBinding.roomId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    booking.roomId = ""
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun saveOrUpdateData() {
        booking.customerName = activityBinding.etCustomerName.text.toString()
        booking.customerNid = activityBinding.etNID.text.toString()
        booking.phone = activityBinding.etPhone.text.toString()
        booking.checkInDate = activityBinding.etCheckInDate.text.toString()
        booking.checkInDate = activityBinding.etCheckOutDate.text.toString()
        if (command == "add") {
            viewModel.bookRoom(booking)
        } else {
            viewModel.updateBooking(booking)
        }
    }
}