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
import com.hotel.evergreenkuakata.utils.Constants
import com.hotel.evergreenkuakata.utils.PrefManager
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
class RoomActivity : BaseActivity<ActivityRoomBinding>() {
    private lateinit var activityBinding: ActivityRoomBinding
    private var room = Room()
    private var command = "add"
    private var spinnerAdapter = SpinnerAdapter()
    private var spinnerData = SpinnerData(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private lateinit var validation: Validation
    private lateinit var ux: UX

    @Inject
    lateinit var tools: Tools

    @Inject
    lateinit var prefManager: PrefManager
    private val viewModel by viewModels<RoomViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        intentData()
        loadData()
        bindUiWithComponents()
        initListeners()
        initObservers()
    }

    override fun setVariables(dataBinding: ActivityRoomBinding) {
        activityBinding = dataBinding
    }

    private fun init() {
        validation = Validation(this, hashMap)
        ux = UX(this)
    }

    private fun intentData() {
        if (intent.extras != null) {
            room =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("room", Room::class.java)!!
                } else {
                    intent.getParcelableExtra("room")!!
                }
        }
    }

    private fun loadData() {
        if (room.roomId.isNotEmpty()) {
            command = "update"
            activityBinding.btnSave.text = getString(R.string.update)
            activityBinding.etName.setText(room.name)
            activityBinding.etPrice.setText("${room.pricePerNight}")
            activityBinding.roomType.setSelection(room.roomCategoryId)

            if (prefManager.getString(Constants.mUserEmail) != "imamundm@gmail.com" || prefManager.getString(
                    Constants.mUserEmail
                ) != "shakil.py@gmail.com"
            ) {
                activityBinding.btnSave.isEnabled = false
                activityBinding.etName.isEnabled = false
                activityBinding.etPrice.isEnabled = false
                activityBinding.roomType.isEnabled = false
                activityBinding.etName.alpha = 0.6f
                activityBinding.btnSave.alpha = 0.6f
                activityBinding.etPrice.alpha = 0.6f
                activityBinding.roomType.alpha = 0.6f
            }
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
            setResult(RESULT_OK)
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
                        spinnerData.getRoomCategoryDataByName(
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
                    viewModel.addRoomStatus.collect { roomStatus ->
                        if (roomStatus?.isSuccess.orFalse()) {
                            doPopupForSuccess()
                            clearAllUiData()
                        }
                    }
                }

                launch {
                    viewModel.updateRoomStatus.collect { roomStatus ->
                        if (roomStatus?.isSuccess.orFalse()) {
                            Toast.makeText(
                                this@RoomActivity,
                                getString(R.string.updated_successfully),
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect{
                        if(it){
                            ux.getLoadingView()
                        }else{
                            ux.removeLoadingView()
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
        ux.getLoadingView()
        if (command == "add") {
            viewModel.addRoom(room)
        } else {
            viewModel.updateRoom(room)
        }
    }
}