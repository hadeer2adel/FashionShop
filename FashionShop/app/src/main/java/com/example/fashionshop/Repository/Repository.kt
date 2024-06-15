package com.example.fashionshop.Repository
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
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.ExchangeRatesResponse
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.Images
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody
import retrofit2.http.Path

interface Repository {
    suspend fun getcustomers(): OneCustomer
    suspend fun createCustomer(customer: CustomerRequest): CustomerResponse
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>
    suspend fun getProducts(): Response<ProductResponse>
    suspend fun getCustomerByEmail(email: String): CustomerResponse
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
    suspend fun getDiscountCodesCount(): PriceRuleCount
    suspend fun getDiscountCodes(): PriceRule
    suspend fun getProductById(id: Long): ProductResponse
    suspend fun createDraftOrders(draftOrder: DraftOrderResponse): DraftOrderResponse
    suspend fun updateDraftOrder(id: Long, draftOrder: DraftOrderResponse): DraftOrderResponse
    suspend fun getDraftOrder(id: Long): DraftOrderResponse
    suspend fun updateCustomer(id: Long, customer: UpdateCustomerRequest): CustomerResponse
    suspend fun getProductImage(@Path("id") id: Long): Images
    suspend  fun getExchangeRates(apiKey: String,symbols :String, base: String): ExchangeRatesResponseX


}