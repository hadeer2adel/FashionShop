package com.example.fashionshop.Repository

import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Service.Networking.NetworkManager

class RepositoryImp constructor(
    private var networkManager: NetworkManager
): Repository {

    companion object{
        private var instance: RepositoryImp?=null
        fun getInstance(productNetworkManager: NetworkManager)
                : RepositoryImp {
            return instance ?: synchronized(this){
                val temp= RepositoryImp(productNetworkManager)
                instance =temp
                temp
            }
        }
    }

    override suspend fun getcustomers(): OneCustomer {
        return networkManager.getcutomers()

    }

    override suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest {
        return networkManager.AddSingleCustomerAdreess(addressRequest)
    }
}