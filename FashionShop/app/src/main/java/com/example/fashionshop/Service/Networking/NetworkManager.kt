package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.customers
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkManager {
    suspend fun getcutomers(): customers

    suspend fun createCustomer(customer: CustomerRequest): CustomerResponse

    suspend fun getCustomerByEmail(email: String): customers

}