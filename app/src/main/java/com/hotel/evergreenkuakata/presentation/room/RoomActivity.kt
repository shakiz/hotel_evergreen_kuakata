package com.hotel.evergreenkuakata.presentation.room

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.databinding.ActivityRoomBinding
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RoomActivity : BaseActivity<ActivityRoomBinding>() {
    private lateinit var activityBinding: ActivityRoomBinding
    private var room = Room()
    private var command = "add"
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    @Inject
    lateinit var tools: Tools

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
        initListeners()
    }

    override fun setVariables(dataBinding: ActivityRoomBinding) {
        activityBinding = dataBinding
    }

    private fun loadData() {
        if (room.roomId.isNotEmpty()) {
            command = "update"
        }
    }

    private fun initListeners(){
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
    }

    private fun saveOrUpdateData() {
        room.name = activityBinding.etName.text.toString()
        room.pricePerNight = activityBinding.etPrice.text.toString().toInt()
        if (command == "add") {
            viewModel.addTenant(tenant)
        } else {
            viewModel.updateTenant(tenant)
        }
    }
}