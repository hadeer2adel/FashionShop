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
        CartListID
    }
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
    }

    companion object {
        @Volatile
        private var INSTANCE : SharedPreferenceManager? = null

        fun getInstance(context: Context) : SharedPreferenceManager {
            return INSTANCE ?: synchronized(context){
                val instance = SharedPreferenceManager(context)
                INSTANCE = instance

                instance
            }

        }
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

    fun isContains(key: Key): Boolean {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.contains(key.name)
    }


    fun saveBoolean(key: Key, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(key.name, value)
            apply()
        }
    }

    fun retrieveBoolean(key: Key, defaultValue: Boolean): Boolean {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key.name, defaultValue)
    }
    fun saveLanguage(languageCode: String) {
        editor?.putString("language", languageCode)?.apply()
    }

    fun getLanguage(): String? {
        return sharedPreferences?.getString("language", null)
    }
    fun saveLanguageUnit(language: String) {
        editor?.putString("languageCode", language)?.apply()
    }

    fun getLanguageUnit(): String? {
        return sharedPreferences?.getString("languageCode", "en")
    }

}