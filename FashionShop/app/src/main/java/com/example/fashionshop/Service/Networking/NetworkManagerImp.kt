package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers
import com.example.fashionshop.network.NetworkService
import com.example.fashionshop.network.RetrofitHelper

class NetworkManagerImp private constructor(): NetworkManager {
    private val networkService : NetworkService by lazy {
        RetrofitHelper.retrofitInstance.create(NetworkService::class.java)
    }

    companion object{

        private var instance: NetworkManagerImp?=null
        fun getInstance(): NetworkManagerImp {
            return instance ?: synchronized(this){
                val temp= NetworkManagerImp()
                instance =temp
                temp
            }
        }
    }

    override suspend fun getcutomers(): OneCustomer {
        val responce= networkService.getSingleCustomer()
        return responce
    }

    override suspend fun AddSingleCustomerAdreess(address1: String, address2: String, city: String, company: String, firstName: String, lastName: String, phone: String, province: String, country: String, zip: String, name: String,province_code:String,country_code:String,
                                                  country_name:String ): AddressRequest {
        val responce= networkService.AddSingleCustomerAdreess(address1,address2,city,company,firstName,lastName,phone,province,country,zip,name,province_code,country_code,country_name)
        return responce
    }
}