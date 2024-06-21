package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.OrderBodyResponse
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.UpdateCustomerRequest
import retrofit2.Response

class FakeNetworkManager (var address: AddressRequest,
                          var updatedAddress: AddressUpdateRequest,
                          var prices :PriceRule) :NetworkManager {
    override suspend fun getcutomers(id: Long): OneCustomer {
        TODO("Not yet implemented")
    }

    override suspend fun AddSingleCustomerAdreess(
        id: Long,
        addressRequest: AddressRequest
    ): AddressRequest {
        return address
    }

    override suspend fun editSingleCustomerAddress(
        cutomerId: Long,
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
       return  updatedAddress
    }

    override suspend fun deleteSingleCustomerAddress(customerId: Long, id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun createCustomer(customer: CustomerRequest): CustomerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getBrands(): Response<BrandResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getBrandProducts(vendor: String): Response<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomerByEmail(email: String): CustomerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getDiscountCodes(): PriceRule {
        return  prices

    }

    override suspend fun getProductById(id: Long): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createDraftOrders(draftOrder: DraftOrderResponse): DraftOrderResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateDraftOrder(
        id: Long,
        draftOrder: DraftOrderResponse
    ): DraftOrderResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getDraftOrder(id: Long): DraftOrderResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateCustomer(
        id: Long,
        customer: UpdateCustomerRequest
    ): CustomerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getExchangeRates(
        apiKey: String,
        symbols: String,
        base: String
    ): ExchangeRatesResponseX {
        TODO("Not yet implemented")
    }

    override suspend fun createCheckoutSession(
        successUrl: String,
        cancelUrl: String,
        customerEmail: String,
        currency: String,
        productName: String,
        productDescription: String,
        unitAmountDecimal: Int,
        quantity: Int,
        mode: String,
        paymentMethodType: String
    ): CheckoutSessionResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomerOrders(userId: Long): Response<OrderResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(order: Map<String, OrderBody>): OrderBodyResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getSingleOrder(orderId: Long): Response<OrderResponse> {
        TODO("Not yet implemented")
    }
}