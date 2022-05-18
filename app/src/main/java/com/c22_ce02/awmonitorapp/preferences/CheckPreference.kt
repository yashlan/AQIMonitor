package com.c22_ce02.awmonitorapp.preferences

import android.annotation.SuppressLint
import android.content.Context

class CheckPreference(context: Context) {



    private val preference = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)

    @SuppressLint("CommitPrefEdits")
    fun setCheck(value: CheckHelper){
        val editor = preference.edit()
        editor.putBoolean(IS_LOGIN.toString(),value.isLogin)
        editor.apply()
    }

    fun getCheck() : CheckHelper {
        val result = CheckHelper()
        result.isLogin = preference.getBoolean(IS_LOGIN.toString(),false)
        return result
    }


    companion object {
        private const val PREF_NAME = "checkPref"
        private const val IS_LOGIN = false
    }
}