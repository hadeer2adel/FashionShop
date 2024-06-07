package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.customers

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

    override suspend fun getcutomers(): customers {
        val responce= networkService.getcustomers()
        return responce
    }
}