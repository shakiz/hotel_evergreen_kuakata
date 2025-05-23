package com.hotel.evergreenkuakata.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hotel.evergreenkuakata.data.model.room.Room
import com.hotel.evergreenkuakata.databinding.AdapterLayoutRoomStatusBinding

class RecyclerAdapterRoomStatus(val list: List<Room>) :
    RecyclerView.Adapter<RecyclerAdapterRoomStatus.VideoItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemViewHolder {
        val binding = AdapterLayoutRoomStatusBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoItemViewHolder, position: Int) {
        val videoItem = list[position]
        holder.bind(videoItem)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class VideoItemViewHolder(
        var binding: AdapterLayoutRoomStatusBinding,
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(room: Room) {
            binding.roomName.text = room.name
        }
    }
}
