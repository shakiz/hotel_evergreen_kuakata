package com.hotel.evergreenkuakata.presentation.booking

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.databinding.ActivityBookingBinding
import com.hotel.evergreenkuakata.utils.DatePickerManager
import com.hotel.evergreenkuakata.utils.SpinnerAdapter
import com.hotel.evergreenkuakata.utils.SpinnerData
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.UX
import com.hotel.evergreenkuakata.utils.Validation
import com.hotel.evergreenkuakata.utils.orFalse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BookingActivity : BaseActivity<ActivityBookingBinding>() {
    private lateinit var activityBinding: ActivityBookingBinding
    private var booking = BookingInfo()
    private var command = "add"
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private lateinit var validation: Validation
    private lateinit var ux: UX

    @Inject
    lateinit var tools: Tools
    private val viewModel by viewModels<BookingViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_booking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        intentData()
        loadData()
        bindUiWithComponents()
        initListeners()
        initObservers()

        viewModel.fetchRoomsWithAvailability(tools.getTodayDate())
        viewModel.fetchAllUsers()
    }

    override fun setVariables(dataBinding: ActivityBookingBinding) {
        activityBinding = dataBinding
    }

    private fun init() {
        validation = Validation(this, hashMap)
        ux = UX(this)
    }

    private fun intentData() {
        if (intent.extras != null) {
            booking =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("bookingInfo", BookingInfo::class.java)!!
                } else {
                    intent.getParcelableExtra("bookingInfo")!!
                }
        }
    }

    private fun loadData() {
        if (booking.bookingId.isNotEmpty()) {
            command = "update"
            activityBinding.btnBook.text = getString(R.string.update)
            activityBinding.etCustomerName.setText(booking.customerName)
            activityBinding.etNID.setText(booking.customerNid)
            activityBinding.etPhone.setText(booking.phone)
            activityBinding.etBookingMoney.setText("${booking.bookingAdvance}")
            activityBinding.etCheckInDate.setText(booking.checkInDate)
            activityBinding.etCheckOutDate.setText(booking.checkOutDate)
        }
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("etCustomerName", "etNID", "etPhone", "etCheckInDate", "etBookingMoney"),
            arrayOf(
                getString(R.string.customer_name_validation),
                getString(R.string.nid_validation),
                getString(R.string.phone_no_validation),
                getString(R.string.checkin_date_validation),
                getString(R.string.booking_money_validation),
            )
        )
        validation.setSpinnerIsNotEmpty(arrayOf("roomId", "bookingStatus", "referredById"))

        spinnerAdapter.setSpinnerAdapter(
            activityBinding.roomId,
            this,
            spinnerData.setSpinnerNoData()
        )

        spinnerAdapter.setSpinnerAdapter(
            activityBinding.bookingStatus,
            this,
            spinnerData.setBookingStatusData()
        )

        if (booking.bookingId.isNotEmpty()){
            val bookingStatusPos = spinnerData.getBookingStatusDataByName(booking.bookingStatus)
            activityBinding.bookingStatus.setSelection(bookingStatusPos)
        }
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        activityBinding.etCheckInDate.setOnClickListener {
            DatePickerManager().showDatePicker(this, object : DatePickerManager.DatePickerCallback {
                override fun onDateSelected(date: String) {
                    activityBinding.etCheckInDate.setText(date)
                    booking.checkInDate = date
                }
            })
        }

        activityBinding.etCheckOutDate.setOnClickListener {
            DatePickerManager().showDatePicker(this, object : DatePickerManager.DatePickerCallback {
                override fun onDateSelected(date: String) {
                    activityBinding.etCheckOutDate.setText(date)
                    booking.checkOutDate = date
                }
            })
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
                    val item = parent.getItemAtPosition(position).toString()
                    if (item != getString(R.string.select_data_1)){
                        booking.roomName = item
                        booking.roomId =
                            viewModel.roomsWithAvailability.value.first { it.room.name == item }.room.roomId
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityBinding.bookingStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val item = parent.getItemAtPosition(position).toString().lowercase()
                    booking.bookingStatus = when(item){
                        getString(R.string.active).lowercase() -> "active"
                        getString(R.string.cancelled).lowercase() -> "cancelled"
                        getString(R.string.checked_in).lowercase() -> "checked_in"
                        getString(R.string.checked_out).lowercase() -> "checked_out"
                        else -> ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        activityBinding.referredById.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val item = parent.getItemAtPosition(position).toString()
                    booking.referredById =
                        viewModel.allUsers.value.first { it.name == item }.userId
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.roomsWithAvailability.collect { availableRooms ->
                        val rooms = ArrayList(availableRooms.map { it.room.name })
                        spinnerAdapter.setSpinnerAdapter(
                            activityBinding.roomId,
                            this@BookingActivity,
                            rooms
                        )

                        if (booking.roomId.isNotEmpty()) {
                            val selectedIndex =
                                viewModel.roomsWithAvailability.value.indexOfFirst { it.room.name == booking.roomName }
                            activityBinding.roomId.setSelection(
                                selectedIndex
                            )
                        }
                    }
                }

                launch {
                    viewModel.allUsers.collect { userList ->
                        val rooms = ArrayList(userList.map { it.name })
                        spinnerAdapter.setSpinnerAdapter(
                            activityBinding.referredById,
                            this@BookingActivity,
                            rooms
                        )

                        if (booking.referredById > 0) {
                            val selectedIndex =
                                viewModel.allUsers.value.indexOfFirst { it.userId == booking.referredById }
                            activityBinding.referredById.setSelection(
                                selectedIndex
                            )
                        }
                    }
                }

                launch {
                    viewModel.bookingStatus.collect { bookingStatus ->
                        if (bookingStatus?.isSuccess.orFalse()) {
                            ux.removeLoadingView()
                            doPopupForSuccess()
                            clearAllUiData()
                        }
                    }
                }

                launch {
                    viewModel.updateBookingStatus.collect { bookingStatus ->
                        if (bookingStatus?.isSuccess.orFalse()) {
                            ux.removeLoadingView()
                            Toast.makeText(
                                this@BookingActivity,
                                getString(R.string.updated_successfully),
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun doPopupForSuccess() {
        val cancel: Button
        val dialog = Dialog(this@BookingActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.booking_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        cancel = dialog.findViewById(R.id.cancelButton)
        cancel.setOnClickListener { dialog.dismiss() }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun clearAllUiData() {
        activityBinding.roomId.setSelection(0)
        activityBinding.bookingStatus.setSelection(0)
        activityBinding.etCustomerName.text.clear()
        activityBinding.etNID.text.clear()
        activityBinding.etPhone.text.clear()
        activityBinding.etCheckInDate.text.clear()
        activityBinding.etCheckOutDate.text.clear()
    }

    private fun saveOrUpdateData() {
        booking.customerName = activityBinding.etCustomerName.text.toString()
        booking.customerNid = activityBinding.etNID.text.toString()
        booking.phone = activityBinding.etPhone.text.toString()
        booking.bookingAdvance = activityBinding.etBookingMoney.text.toString().toInt()
        booking.bookingDate = tools.getTodayDate()
        booking.pricePerNight = viewModel.getTotalAmountByRoomId(booking.roomId)
        ux.getLoadingView()
        if (command == "add") {
            viewModel.bookRoom(booking)
        } else {
            viewModel.updateBooking(booking)
        }
    }
}