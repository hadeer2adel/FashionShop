package com.example.fashionshop.Repository

import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers

interface Repository {
    suspend fun getcustomers(): OneCustomer
    suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest


}