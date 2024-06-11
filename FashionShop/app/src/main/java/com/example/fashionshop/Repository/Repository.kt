package com.example.fashionshop.Repository

import com.example.fashionshop.Model.AddressDefault
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody

interface Repository {
    suspend fun getcustomers(): OneCustomer
    suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest
    suspend fun editSingleCustomerAddress(
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest
    suspend fun deleteSingleCustomerAddress(id:Long)
    suspend fun getDraftOrders(): DraftOrders
    suspend fun deleteSingleCustomerDrafOrder(id:Long)
    suspend fun editSingleCustomerAddressDraftOrder(id:Long,addressRequest: editAddressBody): DraftOrders
    suspend fun editSingleCustomerAddressDraftOrderQuantity(id:Long,quantityRequest: editOrderQuantityBody): DraftOrders



}