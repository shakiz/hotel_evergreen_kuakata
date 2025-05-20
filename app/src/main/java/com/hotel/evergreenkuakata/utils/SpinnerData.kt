package com.hotel.evergreenkuakata.utils

import android.content.Context
import com.hotel.evergreenkuakata.R
import java.util.Calendar
import java.util.Collections

class SpinnerData(private val context: Context) {
    fun setYearData(): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        // Current year
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        spinnerValues.add(context.getString(R.string.select_data_1))
        // Add years dynamically up to the current year
        for (year in 2015..currentYear) {
            spinnerValues.add(year.toString())
        }
        return spinnerValues
    }

    fun setMonthData(selectedYear: Int): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        // Current year and month
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Month is 0-indexed

        spinnerValues.add(context.getString(R.string.select_data_1))

        // Add months dynamically
        for (month in 1..12) {
            // If the selected year is the current year, limit the months to the current month
            if (selectedYear == currentYear && month > currentMonth) {
                break
            }
            // Convert month index to localized month name
            spinnerValues.add(getMonthName(month))
        }

        return spinnerValues
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> context.getString(R.string.january)
            2 -> context.getString(R.string.february)
            3 -> context.getString(R.string.march)
            4 -> context.getString(R.string.april)
            5 -> context.getString(R.string.may)
            6 -> context.getString(R.string.june)
            7 -> context.getString(R.string.july)
            8 -> context.getString(R.string.august)
            9 -> context.getString(R.string.september)
            10 -> context.getString(R.string.october)
            11 -> context.getString(R.string.november)
            12 -> context.getString(R.string.december)
            else -> ""
        }
    }

    fun setTenantTypeData(): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        val tenantTypeArray = arrayOf(
            context.getString(R.string.bachelor),
            context.getString(R.string.large_family),
            context.getString(R.string.small_family),
            context.getString(R.string.female_only),
            context.getString(R.string.male_only),
            context.getString(R.string.others)
        )
        spinnerValues.addAll(tenantTypeArray)
        return spinnerValues
    }

    fun setRoomCategoryData(): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        val roomCategoryArray = arrayOf(
            context.getString(R.string.couple_bed),
            context.getString(R.string.double_bed),
            context.getString(R.string.three_bed)
        )
        spinnerValues.addAll(roomCategoryArray)
        return spinnerValues
    }

    fun getRoomCategoryDataByName(roomType: String): Int {
        val roomCategoryArray = arrayOf(
            context.getString(R.string.couple_bed),
            context.getString(R.string.double_bed),
            context.getString(R.string.three_bed)
        )
        return roomCategoryArray.indexOfFirst { it == roomType }
    }

    fun getRoomCategoryTextDataById(roomType: Int): String {
        return when (roomType) {
            1 -> context.getString(R.string.couple_bed)
            2 -> context.getString(R.string.double_bed)
            3 -> context.getString(R.string.three_bed)
            else -> ""
        }
    }

    fun setRentStatusSpinnerData(): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        val tenantTypeArray = arrayOf(
            context.getString(R.string.paid),
            context.getString(R.string.due),
        )
        spinnerValues.addAll(tenantTypeArray)
        return spinnerValues
    }

    fun setMeterTypeData(): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        val meterTypeArray = arrayOf("Select Data", "Main Meter", "Sub Meter")
        Collections.addAll(spinnerValues, *meterTypeArray)
        return spinnerValues
    }

    fun setSpinnerNoData(): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        val meterTypeArray = arrayOf("No Data")
        Collections.addAll(spinnerValues, *meterTypeArray)
        return spinnerValues
    }

    fun setBookingStatusData(): ArrayList<String> {
        val spinnerValues = ArrayList<String>()
        val roomCategoryArray = arrayOf(
            "Active",
            "Checked In",
            "Checked Out",
            "Cancelled"
        )
        spinnerValues.addAll(roomCategoryArray)
        return spinnerValues
    }

    fun getBookingStatusDataByName(bookingStatus: String): Int {
        val roomCategoryArray = arrayOf(
            "active",
            "checked_in",
            "checked_out",
            "cancelled"
        )
        return roomCategoryArray.indexOfFirst { it == bookingStatus }
    }

    fun getBookingStatusTextDataById(bookingStatus: Int): String {
        return when (bookingStatus) {
            1 -> context.getString(R.string.active)
            2 -> context.getString(R.string.checked_in)
            3 -> context.getString(R.string.checked_out)
            else -> context.getString(R.string.cancelled)
        }
    }
}
