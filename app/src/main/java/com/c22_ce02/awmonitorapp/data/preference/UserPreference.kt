package com.c22_ce02.awmonitorapp.data.preference

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit

class UserPreference(context: Context) {

    private val preference = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    fun isSessionEmpty(): Boolean {
        val userId = preference.getString(NAME_KEY, "").toString()
        val name = preference.getString(EMAIL_KEY, "").toString()
        return userId.isEmpty() && name.isEmpty()
    }

    fun deleteSession(onDelete: (Boolean) -> Unit) {
        preference.edit().clear().apply()
        onDelete.invoke(isSessionEmpty())
    }

    fun saveSession(name: String, email: String, onSave: (String?, String?) -> Unit) {
        preference.edit().putString(NAME_KEY, name).apply()
        preference.edit().putString(EMAIL_KEY, email).apply()
        onSave.invoke(getName(), getEmail())
    }

    fun getName(): String = preference.getString(NAME_KEY, "").toString()

    fun getEmail(): String = preference.getString(EMAIL_KEY, "").toString()

    companion object {
        private const val USER_PREF = "USER_PREF"
        private const val NAME_KEY = "NAME_KEY"
        private const val EMAIL_KEY = "EMAIL_KEY"
    }
}