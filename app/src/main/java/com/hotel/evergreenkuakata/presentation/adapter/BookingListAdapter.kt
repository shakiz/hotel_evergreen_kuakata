package com.hotel.evergreenkuakata.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.data.model.booking.BookingInfo
import com.hotel.evergreenkuakata.databinding.AdapterLayoutBookingListBinding
import com.hotel.evergreenkuakata.utils.DateTimeConstants.APP_DATETIME_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingAdapter : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {
    private var bookingList: List<BookingInfo> = mutableListOf()

    fun setItems(list: List<BookingInfo>) {
        this.bookingList = list
        notifyDataSetChanged()
    }

    inner class BookingViewHolder(val binding: AdapterLayoutBookingListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bookingInfo: BookingInfo) {
            val context = binding.root.context
            binding.tvRoomId.text = context.getString(R.string.room_x)
            binding.tvCustomerName.text =
                context.getString(R.string.customer_name_x, bookingInfo.customerName)
            binding.tvDate.text =
                context.getString(R.string.date_x, getFormattedDate(bookingInfo.createdAt))
            binding.tvPhone.text = context.getString(R.string.phone_x, bookingInfo.phone)
            binding.tvNID.text = context.getString(R.string.nid_x, bookingInfo.customerNid)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = AdapterLayoutBookingListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val room = bookingList[position]
        holder.bind(room)
    }

    private fun getFormattedDate(timesInMilli: Long): String {
        val date = Date(timesInMilli)

        val formatter = SimpleDateFormat(APP_DATETIME_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }

    override fun getItemCount(): Int = bookingList.size
}
