package com.example.fashionshop.Repository
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
import retrofit2.http.Path
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.UpdateCustomerRequest
import kotlinx.coroutines.flow.Flow


interface Repository {
    suspend fun getcustomers(id:Long): OneCustomer
    suspend fun createCustomer(customer: CustomerRequest): Flow<CustomerResponse>
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>
    suspend fun getProducts(): Response<ProductResponse>
    suspend fun getCustomerByEmail(email: String): Flow<CustomerResponse>
    suspend fun AddSingleCustomerAdreess(id:Long,addressRequest: AddressRequest): AddressRequest
    suspend fun editSingleCustomerAddress(
        customerID:Long,
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest
    suspend fun deleteSingleCustomerAddress(customerID: Long,id: Long)
    suspend fun getCustomerOrders(@Path("id") userId: Long): Response<OrderResponse>
    suspend fun getDiscountCodes():Flow <PriceRule>
    suspend fun getProductById(id: Long): Flow<ProductResponse>
    suspend fun createDraftOrders(draftOrder: DraftOrderResponse): Flow<DraftOrderResponse>
    suspend fun updateDraftOrder(id: Long, draftOrder: DraftOrderResponse): Flow<DraftOrderResponse>
    suspend fun getDraftOrder(id: Long): Flow<DraftOrderResponse>
    suspend fun updateCustomer(id: Long, customer: UpdateCustomerRequest): Flow<CustomerResponse>
    suspend  fun getExchangeRates(apiKey: String,symbols :String, base: String): ExchangeRatesResponseX
    suspend fun createCheckoutSession(successUrl: String,cancelUrl: String,customerEmail: String,currency: String,productName: String,productDescription: String,
                                      unitAmountDecimal: Int, quantity: Int,mode: String, paymentMethodType: String
    ): CheckoutSessionResponse
    suspend fun createOrder(order:  Map<String, OrderBody>): OrderBodyResponse

    suspend fun getSingleOrder(@Path("id") orderId: Long): Response<OrderResponse>

}