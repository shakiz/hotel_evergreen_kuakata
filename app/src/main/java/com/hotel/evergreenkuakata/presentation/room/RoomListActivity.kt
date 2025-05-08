package com.hotel.evergreenkuakata.presentation.room

import android.content.Intent
import android.os.Bundle
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
}