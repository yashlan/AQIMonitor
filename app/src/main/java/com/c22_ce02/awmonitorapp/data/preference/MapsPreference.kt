package com.c22_ce02.awmonitorapp.data.preference

import android.content.Context

class MapsPreference(context: Context) {
    private val preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setLastUpdateMaps(value: Long) {
        preference.edit().putLong(LAST_UPDATE_MAPS, value).apply()
    }

    fun getLastUpdateMaps() = preference.getLong(LAST_UPDATE_MAPS, 0)

    companion object {
        private const val PREF_NAME = "MAPS_PREF"
        private const val LAST_UPDATE_MAPS = "LAST_UPDATE_MAPS"
    }
}