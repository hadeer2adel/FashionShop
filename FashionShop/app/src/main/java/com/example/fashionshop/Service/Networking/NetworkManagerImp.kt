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

    override suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest {
        val responce= networkService.AddSingleCustomerAdreess(addressRequest)
        return responce    }
}