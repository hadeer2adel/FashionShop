package com.example.fashionshop.Repository

import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers

interface Repository {
    suspend fun getcustomers(): OneCustomer
    suspend fun AddSingleCustomerAdreess(address1: String, address2: String, city: String, company: String, firstName: String, lastName: String, phone: String, province: String, country: String, zip: String, name: String,province_code:String,country_code:String,
                                         country_name:String): AddressRequest


}