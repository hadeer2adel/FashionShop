package com.example.fashionshop.Repository

import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody
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

    override suspend fun editSingleCustomerAddress(
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
        return networkManager.editSingleCustomerAddress(id,addressRequest)
    }

    override suspend fun deleteSingleCustomerAddress(id: Long) {
        return networkManager.deleteSingleCustomerAddress(id)
    }

    override suspend fun getDraftOrders(): DraftOrders {
        return  networkManager.getDraftOrders()
    }

    override suspend fun deleteSingleCustomerDrafOrder(id: Long) {
        return networkManager.deleteSingleCustomerDrafOrder(id)
    }

    override suspend fun editSingleCustomerAddressDraftOrder(
        id: Long,
        addressRequest: editAddressBody
    ): DraftOrders {
        return networkManager.editSingleCustomerAddressDraftOrder(id,addressRequest)
    }

    override suspend fun editSingleCustomerAddressDraftOrderQuantity(
        id: Long,
        quantityRequest: editOrderQuantityBody
    ): DraftOrders {
        return  networkManager.editSingleCustomerAddressDraftOrderQuantity(id,quantityRequest)
    }
}