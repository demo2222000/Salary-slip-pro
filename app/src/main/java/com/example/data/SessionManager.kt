package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "payslip_user_session"
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
        private const val KEY_WEEKLY_BONUS_RATE = "weekly_bonus_rate"
    }

    fun saveSession(userId: String) {
        prefs.edit().putString(KEY_ACTIVE_USER_ID, userId).apply()
    }

    fun getActiveUserId(): String? {
        return prefs.getString(KEY_ACTIVE_USER_ID, null)
    }

    fun clearSession() {
        prefs.edit().remove(KEY_ACTIVE_USER_ID).apply()
    }

    fun isLoggedIn(): Boolean {
        return !prefs.getString(KEY_ACTIVE_USER_ID, null).isNullOrBlank()
    }

    fun saveWeeklyBonusRate(rate: Double) {
        prefs.edit().putFloat(KEY_WEEKLY_BONUS_RATE, rate.toFloat()).apply()
    }

    fun getWeeklyBonusRate(): Double {
        return prefs.getFloat(KEY_WEEKLY_BONUS_RATE, 250f).toDouble()
    }
}
