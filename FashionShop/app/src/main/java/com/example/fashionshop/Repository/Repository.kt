package com.example.fashionshop.Repository

import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.customers

interface Repository {
    suspend fun getcustomers():customers
    suspend fun createCustomer(customer: CustomerRequest): CustomerResponse


}