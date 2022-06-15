package com.c22_ce02.awmonitorapp.data.preference

import android.content.Context

class PostDataPreference(context: Context) {
    private val preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setLastPost(value: Long) {
        preference.edit().putLong(LAST_POST_KEY, value).apply()
    }

    fun getLastPost() : Long = preference.getLong(LAST_POST_KEY, 0)

    companion object {
        private const val PREF_NAME = "POST_DATA_PREF"
        private const val LAST_POST_KEY = "LAST_POST_KEY"
    }
}