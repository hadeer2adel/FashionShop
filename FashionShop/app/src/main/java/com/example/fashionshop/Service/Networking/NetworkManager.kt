package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody

interface NetworkManager {
    suspend fun getcutomers(): OneCustomer
    suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest

    suspend fun editSingleCustomerAddress(id:Long,addressRequest: AddressDefultRequest): AddressUpdateRequest

    suspend fun deleteSingleCustomerAddress(id:Long)
    suspend fun deleteSingleCustomerDrafOrder(id:Long)
    suspend fun getDraftOrders():DraftOrders
    suspend fun editSingleCustomerAddressDraftOrder(id:Long,addressRequest: editAddressBody): DraftOrders
    suspend fun editSingleCustomerAddressDraftOrderQuantity(id:Long,quantityRequest: editOrderQuantityBody): DraftOrders

}