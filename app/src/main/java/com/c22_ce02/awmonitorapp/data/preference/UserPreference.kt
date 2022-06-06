package com.c22_ce02.awmonitorapp.data.preference

import android.content.Context
import androidx.core.content.edit

class UserPreference(context: Context) {

    private val preference = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    fun isSessionEmpty(): Boolean {
        val userId = preference.getString(NAME_KEY, "").toString()
        val name = preference.getString(EMAIL_KEY, "").toString()
        return userId.isEmpty() && name.isEmpty()
    }

    fun deleteSession(onDelete: () -> Unit) {
        preference.edit {
            clear().apply {
                onDelete.invoke()
            }
        }
    }

    fun saveSession(name: String, email: String, onSave: () -> Unit) {
        preference.edit {
            putString(NAME_KEY, name)
            putString(EMAIL_KEY, email)
        }.apply {
            if (!isSessionEmpty()) {
                onSave.invoke()
            }
        }
    }

    fun getName(): String = preference.getString(NAME_KEY, "").toString()

    fun getEmail(): String = preference.getString(EMAIL_KEY, "").toString()

    companion object {
        private const val USER_PREF = "USER_PREF"
        private const val NAME_KEY = "NAME_KEY"
        private const val EMAIL_KEY = "EMAIL_KEY"
    }
}