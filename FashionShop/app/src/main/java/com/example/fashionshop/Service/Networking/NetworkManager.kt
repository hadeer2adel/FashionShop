package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.customers
import retrofit2.Response
import retrofit2.http.Query
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.DraftOrdersRequest
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody
import retrofit2.http.Path

interface NetworkManager {
    suspend fun getcutomers(): OneCustomer
    suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest

    suspend fun editSingleCustomerAddress(id:Long,addressRequest: AddressDefultRequest): AddressUpdateRequest

    suspend fun deleteSingleCustomerAddress(id:Long)
    suspend fun deleteSingleCustomerDrafOrder(id:Long)
    suspend fun getDraftOrders():DraftOrders
    suspend fun editSingleCustomerAddressDraftOrder(id:Long,addressRequest: editAddressBody): DraftOrders
    suspend fun editSingleCustomerAddressDraftOrderQuantity(id:Long,quantityRequest: editOrderQuantityBody): DraftOrders

    suspend fun createCustomer(customer: CustomerRequest): CustomerResponse
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getProducts(): Response<ProductResponse>
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>

    suspend fun getCustomerByEmail(email: String): customers

    suspend fun getProductById(id: Long): ProductResponse

    suspend fun createDraftOrders(draftOrders: DraftOrdersRequest): DraftOrderResponse
    suspend fun updateDraftOrder(@Path("id") id: Long): DraftOrderResponse
    suspend fun getDraftOrder(@Path("id") id: Long): DraftOrderResponse

    suspend fun updateCustomer(id: Long, customer: UpdateCustomerRequest): CustomerResponse

}