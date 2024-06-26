package com.example.fashionshop.Service.Caching

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(private val context: Context) {
    private val FILE_NAME = "UserInfo"

    enum class Key {
        ID,
        NAME,
        EMAIL,
        PHONE,
        CURRENCY,
        IS_LOGGED_IN,
        FavListID,
        CartListID,
        Language,
        LanguageCode
    }

    fun save(key: Key, value: String) {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(key.name, value)
            apply()
        }
    }

    fun retrieve(key: Key, defaultValue: String): String {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key.name, defaultValue) ?: defaultValue
    }

    fun clear(){
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}