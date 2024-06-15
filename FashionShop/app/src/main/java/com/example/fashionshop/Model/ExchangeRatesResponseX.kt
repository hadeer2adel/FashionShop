package com.example.fashionshop.Model

data class ExchangeRatesResponseX(
    val base: String,
    val date: String,
    val rates: RatesX,
    val success: Boolean,
    val timestamp: Int
)