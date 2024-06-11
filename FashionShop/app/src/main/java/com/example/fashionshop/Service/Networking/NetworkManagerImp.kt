package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody
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

    override suspend fun editSingleCustomerAddress(
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
        val responce= networkService.editSingleCustomerAddress(id,addressRequest)
        return responce    }

    override suspend fun deleteSingleCustomerAddress(id: Long) {
        networkService.deleteSingleCustomerAddress(id)
    }

    override suspend fun deleteSingleCustomerDrafOrder(id: Long)
    {
        networkService.deleteSingleCustomerDrafOrder(id)

    }

    override suspend fun getDraftOrders(): DraftOrders {
        val responce= networkService.getDraftOrders()
        return responce        }

    override suspend fun editSingleCustomerAddressDraftOrder(
        id: Long,
        addressRequest: editAddressBody
    ): DraftOrders {
        val responce= networkService.editSingleCustomerAddressDraftOrder(id,addressRequest)
        return responce
    }

    override suspend fun editSingleCustomerAddressDraftOrderQuantity(
        id: Long,
        quantityRequest: editOrderQuantityBody
    ): DraftOrders {
        val responce =networkService.editSingleCustomerAddressDraftOrderQuantity(id,quantityRequest)
        return responce
    }
}