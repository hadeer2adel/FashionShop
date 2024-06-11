package com.example.fashionshop.Repository

import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
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

    override suspend fun getcustomers(): customers {
        return networkManager.getcutomers()

    }

    override suspend fun createCustomer(
        customer: CustomerRequest
    ): CustomerResponse {
        return networkManager.createCustomer(customer)
    }

    override suspend fun getCustomerByEmail(email: String): customers {
        return networkManager.getCustomerByEmail(email)
    }
}