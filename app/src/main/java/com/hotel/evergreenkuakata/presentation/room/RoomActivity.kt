package com.hotel.evergreenkuakata.presentation.room

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
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.databinding.ActivityRoomBinding
import com.hotel.evergreenkuakata.utils.SpinnerAdapter
import com.hotel.evergreenkuakata.utils.SpinnerData
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.Validation
import com.hotel.evergreenkuakata.utils.orFalse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RoomActivity : BaseActivity<ActivityRoomBinding>() {
    private lateinit var activityBinding: ActivityRoomBinding
    private var room = Room()
    private var command = "add"
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)

    @Inject
    lateinit var tools: Tools
    private val viewModel by viewModels<RoomViewModel>()

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
        loadData()
        bindUiWithComponents()
        initListeners()
        initObservers()
    }

    override fun setVariables(dataBinding: ActivityRoomBinding) {
        activityBinding = dataBinding
    }

    private fun loadData() {
        if (room.roomId.isNotEmpty()) {
            command = "update"
        }
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("etName", "etPrice"), arrayOf(
                getString(R.string.room_name_validation),
                getString(R.string.room_rent_amount_validation),
            )
        )
        validation.setSpinnerIsNotEmpty(arrayOf("roomType"))

        spinnerAdapter.setSpinnerAdapter(
            activityBinding.roomType,
            this,
            spinnerData.setRoomCategoryData()
        )
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            finish()
        }

        activityBinding.btnSave.setOnClickListener {
            if (validation.isValid) {
                if (tools.hasConnection()) {
                    saveOrUpdateData()
                } else {
                    Toast.makeText(
                        this@RoomActivity,
                        getString(R.string.no_internet_title),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        activityBinding.roomType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    room.roomCategoryId =
                        spinnerData.getRoomCategoryDataById(
                            parent.getItemAtPosition(position).toString()
                        )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.addRoomStatus.collect { bookingStatus ->
                        if (bookingStatus?.isSuccess.orFalse()) {
                            doPopupForSuccess()
                            clearAllUiData()
                        }
                    }
                }
            }
        }
    }

    private fun doPopupForSuccess() {
        val cancel: Button
        val dialog = Dialog(this@RoomActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.room_save_confirmation_layout)
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
        activityBinding.etName.text.clear()
        activityBinding.etPrice.text.clear()
        activityBinding.roomType.setSelection(0)
    }

    private fun saveOrUpdateData() {
        room.name = activityBinding.etName.text.toString()
        room.pricePerNight = activityBinding.etPrice.text.toString().toInt()
        if (command == "add") {
            viewModel.addRoom(room)
        } else {
            viewModel.updateRoom(room)
        }
    }
}