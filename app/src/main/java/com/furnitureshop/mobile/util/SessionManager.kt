package com.furnitureshop.mobile.util

import android.content.Context

object SessionManager {
    private const val PREFS = "laxmi_session"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "name"
    private const val KEY_ADMIN = "is_admin"
    private const val KEY_HOST = "is_host"

    fun login(context: Context, email: String, name: String, isAdmin: Boolean, isHost: Boolean = false) {
        prefs(context).edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .putBoolean(KEY_ADMIN, isAdmin)
            .putBoolean(KEY_HOST, isHost)
            .apply()
    }

    fun updateName(context: Context, name: String) {
        prefs(context).edit().putString(KEY_NAME, name).apply()
    }

    fun setHost(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_HOST, value).apply()
    }

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun isLoggedIn(context: Context): Boolean = email(context) != null
    fun email(context: Context): String? = prefs(context).getString(KEY_EMAIL, null)
    fun name(context: Context): String? = prefs(context).getString(KEY_NAME, null)
    fun isAdmin(context: Context): Boolean = prefs(context).getBoolean(KEY_ADMIN, false)
    fun isHost(context: Context): Boolean = prefs(context).getBoolean(KEY_HOST, false)

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
