package com.example.fashionshop.repository

import com.example.fashionshop.model.customers
import com.example.fashionshop.network.RemoteDataSource

class RepositoryImp constructor(
    private var remoteDataSource: RemoteDataSource
): Repository {

    companion object{
        private var instance: RepositoryImp?=null
        fun getInstance( productRemoteDataSource: RemoteDataSource    )
                : RepositoryImp {
            return instance ?: synchronized(this){
                val temp= RepositoryImp(productRemoteDataSource)
                instance =temp
                temp
            }
        }
    }

    override suspend fun getcustomers(): customers {
        return remoteDataSource.getcutomers()

    }
}