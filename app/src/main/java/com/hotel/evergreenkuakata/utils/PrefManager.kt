package com.hotel.evergreenkuakata.utils

import android.content.Context
import com.hotel.evergreenkuakata.utils.Constants.PREF_NAME
import javax.inject.Inject

class PrefManager @Inject constructor(context: Context) {
    private val PRIVATE_MODE = 0
    private val pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private val editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit()
    operator fun set(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    operator fun set(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    operator fun set(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    operator fun set(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.commit()
    }

    fun getBoolean(key: String?): Boolean {
        return pref.getBoolean(key, false)
    }

    fun getString(key: String?): String {
        return pref.getString(key, "") ?: "bn"
    }

    fun getInt(key: String?): Int {
        return pref.getInt(key, 0)
    }

    fun getLong(key: String?): Long {
        return pref.getLong(key, 0)
    }
}
