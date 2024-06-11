package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.customers
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NetworkService {
    @GET("customers.json")
    suspend fun getcustomers(): customers

    @POST("customers.json")
    suspend fun createCustomer(
        @Body customer: CustomerRequest
    ): CustomerResponse

    @GET("smart_collections.json")
    suspend fun getBrands(): Response<BrandResponse>

    @GET("products.json")
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>
    @GET("products.json")
    suspend fun getProducts(): Response<ProductResponse>
    @GET("customers/search.json")
    suspend fun getCustomerByEmail(
        @Query("email") email: String
    ): customers

}
