package com.example.fashionshop.network

import com.example.fashionshop.model.customers

class RemoteDataSourceImp private constructor():RemoteDataSource{
    private val shopify_services :Api_Service by lazy {
        RetrofitHelper.retrofitInstance.create(Api_Service::class.java)
    }

    companion object{

        private var instance:RemoteDataSourceImp?=null
        fun getInstance():RemoteDataSourceImp{
            return instance?: synchronized(this){
                val temp=RemoteDataSourceImp()
                instance=temp
                temp
            }
        }
    }

    override suspend fun getcutomers(): customers {
        val responce= shopify_services.getcustomers()
        return responce
    }
}