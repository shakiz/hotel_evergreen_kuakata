package com.hotel.evergreenkuakata.presentation.booking

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
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
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.filterList
import com.hotel.evergreenkuakata.utils.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BookingListActivity : BaseActivity<ActivityBookingListBinding>(), BookingAdapter.BookingCallbacks {
    private lateinit var activityBinding: ActivityBookingListBinding
    private lateinit var bookingAdapter: BookingAdapter
    private val viewModel by viewModels<BookingViewModel>()
    private lateinit var spinnerData: SpinnerData
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    @Inject
    lateinit var tools: Tools

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_booking_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initVariables()
        bindUiWithComponents()
        initListeners()
        setRecyclerAdapter()
        initObservers()
        setupDebouncedSearch()
        viewModel.fetchAllBookings()
    }

    override fun setVariables(dataBinding: ActivityBookingListBinding) {
        activityBinding = dataBinding
    }

    private fun initVariables() {
        spinnerData = SpinnerData(this)
    }

    private fun bindUiWithComponents(){
        activityBinding.searchLayout.SearchName.hint = getString(R.string.search_customer_name)
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        activityBinding.searchLayout.refreshButton.setOnClickListener {
            if (tools.hasConnection()) {
                activityBinding.searchLayout.SearchName.setText("")
                Tools.hideKeyboard(this@BookingListActivity)
                viewModel.fetchAllBookings()
                Toast.makeText(
                    this@BookingListActivity,
                    getString(R.string.list_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@BookingListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun setupDebouncedSearch() {
        activityBinding.searchLayout.SearchName.textChanges()
            .debounce(100)
            .onEach { query ->
                val originalList = viewModel.allBookings.value
                val filteredList =
                    filterList(query.toString().lowercase(), originalList) { room, q ->
                        room.customerName.lowercase().contains(q, ignoreCase = true)
                    }
                bookingAdapter.setItems(filteredList)
            }
            .launchIn(coroutineScope)
    }

    override fun onItemClick(bookingInfo: BookingInfo) {
        startActivity(
            Intent(
                this@BookingListActivity,
                BookingActivity::class.java
            ).putExtra("bookingInfo", bookingInfo)
        )
    }

    override fun onItemDelete(bookingInfo: BookingInfo) {
        doPopUpForDeleteConfirmation(bookingInfo)
    }

    private fun doPopUpForDeleteConfirmation(bookingInfo: BookingInfo) {
        val cancel: Button
        val delete: Button
        val dialog = Dialog(this@BookingListActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        cancel = dialog.findViewById(R.id.cancelButton)
        delete = dialog.findViewById(R.id.deleteButton)
        cancel.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener {
            viewModel.deleteBooking(bookingId = bookingInfo.bookingId)
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
}