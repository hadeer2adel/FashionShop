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

    override suspend fun AddSingleCustomerAdreess(address1: String, address2: String, city: String, company: String, firstName: String, lastName: String, phone: String, province: String, country: String, zip: String,
                                                  name: String,province_code:String,country_code:String,
                                                  country_name:String): AddressRequest {
        return networkManager.AddSingleCustomerAdreess(address1,address2,city,company,firstName,lastName,phone,province,country,zip,name, province_code,country_code,country_name)
    }
}