package com.hotel.evergreenkuakata.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.*

class DatePickerManager {
    fun showDatePicker(context: Context, callback: DatePickerCallback) {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }

                val dateFormat =
                    SimpleDateFormat(DateTimeConstants.APP_DATE_FORMAT, Locale.getDefault())
                val formattedDate = dateFormat.format(selectedCalendar.time)
                callback.onDateSelected(formattedDate)
            },
            year, month, day
        ).show()
    }

    interface DatePickerCallback {
        fun onDateSelected(date: String)
    }
}
