package com.hotel.evergreenkuakata.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.databinding.AdapterLayoutRoomListBinding
import com.hotel.evergreenkuakata.utils.DateTimeConstants.APP_DATETIME_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private var roomCallBacks: RoomCallBacks? = null
    private var roomList: List<Room> = mutableListOf()


    interface RoomCallBacks {
        fun onDelete(room: Room)
        fun onEdit(room: Room)
        fun onItemClick(room: Room)
    }

    fun setOnRoomClick(roomCallBacks: RoomCallBacks?) {
        this.roomCallBacks = roomCallBacks
    }

    fun setItems(list: List<Room>) {
        this.roomList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding =
            AdapterLayoutRoomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.bind(room)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    inner class RoomViewHolder(private val binding: AdapterLayoutRoomListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: Room) {
            val context = binding.root.context
//            Glide
//                .with(binding.root.context)
//                .load(room.imageUrl)
//                .centerCrop()
//                .into(binding.ivRoom)
            binding.tvRoomName.text = room.name
            binding.tvRoomPrice.text = context.getString(R.string.x_price, room.pricePerNight)
            binding.tvRoomType.text = context.getString(R.string.category_x, room.roomCategoryText)
            binding.tvRoomCreatedDate.text =
                context.getString(R.string.date_x, getFormattedDate(room.createdAt))
            binding.root.setOnClickListener {
                roomCallBacks?.onItemClick(room)
            }

            binding.ivDelete.setOnClickListener {
                roomCallBacks?.onDelete(room)
            }
        }
    }

    private fun getFormattedDate(timesInMilli: Long): String {
        val date = Date(timesInMilli)

        val formatter = SimpleDateFormat(APP_DATETIME_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }
}
