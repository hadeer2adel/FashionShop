package com.example.fashionshop.Model

data class ExchangeRatesResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)