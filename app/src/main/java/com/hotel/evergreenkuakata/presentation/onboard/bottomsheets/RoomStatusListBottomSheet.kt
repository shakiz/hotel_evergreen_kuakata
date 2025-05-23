package com.hotel.evergreenkuakata.presentation.onboard.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.databinding.RoomStatusListBottomSheetBinding
import com.hotel.evergreenkuakata.presentation.adapter.RecyclerAdapterRoomStatus

enum class RoomStatusViewType {
    BOOKED, AVAILABLE
}

class RoomStatusListBottomSheet :
    BottomSheetDialogFragment() {

    private var _binding: RoomStatusListBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var roomList: ArrayList<Room>
    private lateinit var roomStatusViewType: String

    companion object {
        private const val ARG_ROOM_LIST = "arg_room_list"
        private const val ARG_VIEW_TYPE = "arg_view_type"

        fun newInstance(
            roomList: ArrayList<Room>,
            viewType: RoomStatusViewType
        ): RoomStatusListBottomSheet {
            val fragment = RoomStatusListBottomSheet()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_ROOM_LIST, roomList)
                putString(ARG_VIEW_TYPE, viewType.name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetWithTopHandleStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RoomStatusListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomList = arguments?.getParcelableArrayList(ARG_ROOM_LIST)!!
        roomStatusViewType = arguments?.getString(ARG_VIEW_TYPE)!!
        initListeners()
        setRecyclerAdapter()
        bindUiWithComponents()
    }

    private fun initListeners() {

    }

    private fun bindUiWithComponents(){
        if (roomList.isNotEmpty()) {
            binding.mRecyclerView.visibility = View.VISIBLE
            binding.noDataLayout.root.visibility =
                View.GONE
        } else {
            binding.mRecyclerView.visibility = View.GONE
            binding.noDataLayout.root.visibility =
                View.VISIBLE
        }
    }

    private fun setRecyclerAdapter() {
        val recyclerAdapterRoomStatus = RecyclerAdapterRoomStatus(roomList)
        binding.mRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3)
        binding.mRecyclerView.adapter = recyclerAdapterRoomStatus
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
