package com.example.fashionshop.Model

import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int

)