package com.example.fashionshop.Model

import android.content.Context
import com.example.fashionshop.Service.Caching.SharedPreferenceManager

class CustomerData private constructor(_context: Context) {
    private val manager: SharedPreferenceManager

    var id: Long = 0
        get() = field
        set(value) {
            field = value
            manager.save(SharedPreferenceManager.Key.ID, value.toString())
        }
    var name: String = ""
        get() = field
        set(value) {
            field = value
            manager.save(SharedPreferenceManager.Key.NAME, value)
        }
    var email: String = ""
        get() = field
        set(value) {
            field = value
            manager.save(SharedPreferenceManager.Key.EMAIL, value)
        }
    var phone: String = ""
        get() = field
        set(value) {
            field = value
            manager.save(SharedPreferenceManager.Key.PHONE, value)
        }
    var currency: String = ""
        get() = field
        set(value) {
            field = value
            manager.save(SharedPreferenceManager.Key.CURRENCY, value)
        }

    init {
        manager = SharedPreferenceManager(_context)
        id = manager.retrieve(SharedPreferenceManager.Key.ID, "0").toLong()
        name = manager.retrieve(SharedPreferenceManager.Key.NAME, "")
        email = manager.retrieve(SharedPreferenceManager.Key.EMAIL, "")
        phone = manager.retrieve(SharedPreferenceManager.Key.PHONE, "")
        currency = manager.retrieve(SharedPreferenceManager.Key.CURRENCY, "")
    }

    companion object {
        @Volatile
        private var instance: CustomerData? = null
        fun getInstance(context: Context): CustomerData {
            if (instance == null)
                instance = CustomerData(context)
            return instance as CustomerData
        }
    }

    fun isLogedIn() = manager.isContains(SharedPreferenceManager.Key.ID)
}