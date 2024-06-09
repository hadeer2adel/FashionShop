package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers
import retrofit2.http.Body

interface NetworkManager {
    suspend fun getcutomers(): OneCustomer
    suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest


}