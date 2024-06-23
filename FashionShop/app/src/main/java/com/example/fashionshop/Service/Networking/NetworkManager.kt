package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import retrofit2.Response
import retrofit2.http.Query
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.OrderBodyResponse
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.UpdateCustomerRequest
import retrofit2.http.Path

interface NetworkManager {
    suspend fun getcutomers( id: Long): OneCustomer
    suspend fun AddSingleCustomerAdreess( id: Long,addressRequest: AddressRequest): AddressRequest
    suspend fun editSingleCustomerAddress(cutomerId:Long,id:Long,addressRequest: AddressDefultRequest): AddressUpdateRequest
    suspend fun deleteSingleCustomerAddress(customerId:Long,id:Long)
    suspend fun createCustomer(customer: CustomerRequest): CustomerResponse
    suspend fun getBrands(): BrandResponse
    suspend fun getProducts(): ProductResponse
    suspend fun getBrandProducts(@Query("vendor") vendor: String): ProductResponse
    suspend fun getCustomerByEmail(email: String): CustomerResponse
    suspend fun getDiscountCodes(): PriceRule
    suspend fun getProductById(id: Long): ProductResponse
    suspend fun createDraftOrders(draftOrder: DraftOrderResponse): DraftOrderResponse
    suspend fun updateDraftOrder(id: Long, draftOrder: DraftOrderResponse): DraftOrderResponse
    suspend fun getDraftOrder(id: Long): DraftOrderResponse
    suspend fun updateCustomer(id: Long, customer: UpdateCustomerRequest): CustomerResponse
    suspend  fun getExchangeRates(apiKey: String,symbols :String, base: String): ExchangeRatesResponseX
    suspend fun createCheckoutSession(successUrl: String,cancelUrl: String,customerEmail: String,currency: String,productName: String,productDescription: String,
                                      unitAmountDecimal: Int, quantity: Int,mode: String, paymentMethodType: String
    ): CheckoutSessionResponse

    suspend fun getCustomerOrders(@Path("id") userId: Long): OrderResponse
    suspend fun createOrder(order:  Map<String, OrderBody>): OrderBodyResponse

    suspend fun getSingleOrder(@Path("id") orderId: Long): OrderResponse

}