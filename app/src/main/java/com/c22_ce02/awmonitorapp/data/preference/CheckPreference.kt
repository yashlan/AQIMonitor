package com.c22_ce02.awmonitorapp.data.preference

import android.content.Context

class CheckPreference(context: Context) {

    private val preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setCheckBoarding(value: CheckHelper) {
        val editor = preference.edit()
        editor.putBoolean(BOARDING_KEY, value.isUserFinishBoarding)
        editor.apply()
    }

    fun getCheckBoarding(): CheckHelper {
        val result = CheckHelper()
        result.isUserFinishBoarding = preference.getBoolean(BOARDING_KEY, false)
        return result
    }

    fun setCheckGuide(value: CheckHelper) {
        val editor = preference.edit()
        editor.putBoolean(GUIDE_KEY, value.isUserFinishGuide)
        editor.apply()
    }

    fun getCheckGuide(): CheckHelper {
        val result = CheckHelper()
        result.isUserFinishGuide = preference.getBoolean(GUIDE_KEY, false)
        return result
    }


    companion object {
        private const val PREF_NAME = "checkPref"
        private const val BOARDING_KEY = "BOARDING_KEY"
        private const val GUIDE_KEY = "GUIDE_KEY"
    }
}