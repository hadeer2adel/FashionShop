package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.customers
import retrofit2.Response

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

    override suspend fun createCustomer(
        customer: CustomerRequest
    ): CustomerResponse {
        return networkService.createCustomer(customer)
    }

    override suspend fun getBrands(): Response<BrandResponse> {
        return networkService.getBrands()
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        return networkService.getProducts()
    }

    override suspend fun getBrandProducts(vendor: String): Response<ProductResponse> {
        return  networkService.getBrandProducts(vendor)
    }


    override suspend fun getCustomerByEmail(email: String): customers {
        return networkService.getCustomerByEmail(email)
    }

}