package com.go26.chatapp.util

import android.content.Context
import com.google.gson.Gson
import com.securepreferences.SecurePreferences

/**
 * Created by daigo on 2018/01/14.
 */
class SecurePrefs(context: Context) {
    @get:Synchronized private val pref = SecurePreferences(context)
    private val gson = Gson()

    companion object {
        const val PREF_FILE = "kotlinGroupChat"
    }

    fun put(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    fun get(key: String): String = pref.getString(key, "")

    fun clear() = pref.destroyKeys()


}