package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.ExchangeRatesResponse
import com.example.fashionshop.Model.ExchangeRatesResponseX
import retrofit2.http.GET
import retrofit2.http.Query

interface networkService2 {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("apikey") apiKey: String,
        @Query("symbols") symbols: String?,
        @Query("base") base: String?
    ): ExchangeRatesResponseX
}