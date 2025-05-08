package com.hotel.evergreenkuakata.presentation.room

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.databinding.ActivityRoomListBinding
import com.hotel.evergreenkuakata.presentation.adapter.RoomAdapter
import com.hotel.evergreenkuakata.utils.SpinnerData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RoomListActivity : BaseActivity<ActivityRoomListBinding>(), RoomAdapter.RoomCallBacks {
    private lateinit var activityBinding: ActivityRoomListBinding
    private lateinit var roomAdapter: RoomAdapter
    private val viewModel by viewModels<RoomViewModel>()
    private lateinit var spinnerData: SpinnerData

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_room_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initVariables()
        initListeners()
        setRecyclerAdapter()
        initObservers()
        viewModel.fetchAllRooms()
    }

    override fun setVariables(dataBinding: ActivityRoomListBinding) {
        activityBinding = dataBinding
    }

    private fun initVariables() {
        spinnerData = SpinnerData(this)
    }

    private fun initListeners() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        activityBinding.toolBar.setNavigationOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.allRooms.collect { rooms ->
                rooms.forEach { room ->
                    room.roomCategoryText =
                        spinnerData.getRoomCategoryTextDataById(room.roomCategoryId)
                }
                roomAdapter.setItems(rooms)
            }
        }
    }

    private fun setRecyclerAdapter() {
        roomAdapter = RoomAdapter()
        activityBinding.mRecyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.mRecyclerView.adapter = roomAdapter
        roomAdapter.setOnRoomClick(this)
    }

    override fun onDelete(room: Room) {
        doPopUpForDeleteConfirmation(room)
    }

    override fun onEdit(room: Room) {

    }

    override fun onItemClick(room: Room) {
        startActivity(
            Intent(
                this@RoomListActivity,
                RoomActivity::class.java
            ).putExtra("room", room)
        )
    }

    private fun doPopUpForDeleteConfirmation(room: Room) {
        val cancel: Button
        val delete: Button
        val dialog = Dialog(this@RoomListActivity, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_confirmation_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        cancel = dialog.findViewById(R.id.cancelButton)
        delete = dialog.findViewById(R.id.deleteButton)
        cancel.setOnClickListener { dialog.dismiss() }
        delete.setOnClickListener {
            viewModel.deleteRoom(roomId = room.roomId)
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