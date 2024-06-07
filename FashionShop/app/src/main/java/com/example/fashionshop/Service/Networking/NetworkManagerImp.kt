package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.customers

class NetworkManagerImp private constructor(): NetworkManager {
    private val shopify_services : Api_Service by lazy {
        RetrofitHelper.retrofitInstance.create(Api_Service::class.java)
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

    override suspend fun getcutomers(): customers {
        val responce= shopify_services.getcustomers()
        return responce
    }
}