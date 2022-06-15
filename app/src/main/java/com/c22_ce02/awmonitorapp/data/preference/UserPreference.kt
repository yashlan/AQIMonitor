package com.c22_ce02.awmonitorapp.data.preference

import android.content.Context

class UserPreference(context: Context) {

    private val preference = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    fun isSessionEmpty(): Boolean {
        val userId = preference.getString(NAME_KEY, null)
        val name = preference.getString(EMAIL_KEY, null)
        val avatarName = preference.getString(AVATAR_KEY, null)
        return userId == null && name == null && avatarName == null
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

    fun saveAvatar(avatarName: String, onSave: (String?) -> Unit) {
        preference.edit().putString(AVATAR_KEY, avatarName).apply()
        onSave.invoke(getAvatar())
    }

    fun getName(): String? = preference.getString(NAME_KEY, null)

    fun getEmail(): String? = preference.getString(EMAIL_KEY, null)

    fun getAvatar(): String? = preference.getString(AVATAR_KEY, null)

    companion object {
        private const val USER_PREF = "USER_PREF"
        private const val NAME_KEY = "NAME_KEY"
        private const val EMAIL_KEY = "EMAIL_KEY"
        private const val AVATAR_KEY = "AVATAR_KEY"
    }
}