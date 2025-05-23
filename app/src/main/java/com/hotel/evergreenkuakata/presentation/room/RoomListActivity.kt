package com.hotel.evergreenkuakata.presentation.room

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
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.databinding.ActivityRoomListBinding
import com.hotel.evergreenkuakata.presentation.adapter.RoomAdapter
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
class RoomListActivity : BaseActivity<ActivityRoomListBinding>(), RoomAdapter.RoomCallBacks {
    private lateinit var activityBinding: ActivityRoomListBinding
    private lateinit var roomAdapter: RoomAdapter
    private val viewModel by viewModels<RoomViewModel>()
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
        get() = R.layout.activity_room_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initVariables()
        bindUiWithComponents()
        initListeners()
        setRecyclerAdapter()
        initObservers()
        viewModel.fetchAllRooms()
        setupDebouncedSearch()
    }

    override fun setVariables(dataBinding: ActivityRoomListBinding) {
        activityBinding = dataBinding
    }

    private fun initVariables() {
        spinnerData = SpinnerData(this)
    }

    private fun bindUiWithComponents(){
        activityBinding.searchLayout.SearchName.hint = getString(R.string.search_room_name)
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
                Tools.hideKeyboard(this@RoomListActivity)
                viewModel.fetchAllRooms()
                Toast.makeText(
                    this@RoomListActivity,
                    getString(R.string.list_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@RoomListActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun setupDebouncedSearch() {
        activityBinding.searchLayout.SearchName.textChanges()
            .debounce(100)
            .onEach { query ->
                val originalList = viewModel.allRooms.value
                val filteredList =
                    filterList(query.toString().lowercase(), originalList) { room, q ->
                        room.name.lowercase().contains(q, ignoreCase = true)
                    }

                if (filteredList.isNotEmpty()) {
                    activityBinding.mRecyclerView.visibility = View.VISIBLE
                    activityBinding.noDataLayout.root.visibility =
                        View.GONE
                } else {
                    activityBinding.mRecyclerView.visibility = View.GONE
                    activityBinding.noDataLayout.root.visibility =
                        View.VISIBLE
                }
                roomAdapter.setItems(filteredList)
            }
            .launchIn(coroutineScope)
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