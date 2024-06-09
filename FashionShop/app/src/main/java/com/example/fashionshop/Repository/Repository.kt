package com.example.fashionshop.Repository

import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.customers
import retrofit2.Response
import retrofit2.http.Query

interface Repository {
    suspend fun getcustomers():customers
    suspend fun createCustomer(customer: CustomerRequest): CustomerResponse
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>

    suspend fun getProducts(): Response<ProductResponse>

}