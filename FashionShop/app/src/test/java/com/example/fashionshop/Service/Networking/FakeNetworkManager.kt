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
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Model.UpdateCustomerRequest
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response
import kotlin.random.Random

class FakeNetworkManager (var address: AddressRequest,
                          var updatedAddress: AddressUpdateRequest,
                          var prices :PriceRule ,
                          var customer:OneCustomer,
                          var checkoutSession:CheckoutSessionResponse,
                          var exchanges:ExchangeRatesResponseX,
                          private var customers: MutableList<CustomerResponse.Customer>,
                          private var draftOrders: MutableList<DraftOrderResponse>,
                          private var brands : BrandResponse,
                          private var products : ProductResponse,
                          private var orders : OrderResponse,
                          private var orderResult : OrderBodyResponse,
                          private var productsList: MutableList<ProductResponse>


) :NetworkManager {
    override suspend fun getcutomers(id: Long): OneCustomer {
    return customer
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
    }

    override suspend fun createCustomer(customerRequest: CustomerRequest): CustomerResponse {
        val customer = CustomerResponse.Customer(
            (customers.size + 1).toLong(),
            customerRequest.customer.first_name!!,
            customerRequest.customer.last_name!!,
            customerRequest.customer.email!!,
            "",
            "EGY",
            0L,
            0L
        )
        customers.add(customer)
        return CustomerResponse(customer)
    }

    override suspend fun getBrands(): BrandResponse {
        return brands
    }

    override suspend fun getProducts(): ProductResponse {
       return products
    }

    override suspend fun getBrandProducts(vendor: String): ProductResponse {
        return products
    }

    override suspend fun getCustomerByEmail(email: String): CustomerResponse {
        customers.forEach{ customer ->
            if (customer.email.equals(email)){
                return CustomerResponse(customers = listOf(customer))
            }
        }
        return CustomerResponse()
    }

    override suspend fun getDiscountCodes(): PriceRule {
        return  prices

    }

    override suspend fun getProductById(id: Long): ProductResponse {
        return productsList.get((id - 1).toInt())
    }

    override suspend fun createDraftOrders(draftOrder: DraftOrderResponse): DraftOrderResponse {
        val id = Random.nextLong(1L, 10000L)
        val fakeDraftOrderResponse = DraftOrderResponse(DraftOrderResponse.DraftOrder(id = id))
        return fakeDraftOrderResponse
    }

    override suspend fun updateDraftOrder(
        id: Long,
        draftOrder: DraftOrderResponse
    ): DraftOrderResponse {
        draftOrders.set((id - 1).toInt(), draftOrder)
        return draftOrders.get((id - 1).toInt())
    }

    override suspend fun getDraftOrder(id: Long): DraftOrderResponse {
        return draftOrders.get((id - 1).toInt())
    }

    override suspend fun updateCustomer(
        id: Long,
        updateCustomerRequest: UpdateCustomerRequest
    ): CustomerResponse {
        var customer = customers.get(id.toInt() - 1)
        customer.note = updateCustomerRequest.customer.note!!
        customer.multipass_identifier = updateCustomerRequest.customer.multipass_identifier!!
        return CustomerResponse(customer)
    }

    override suspend fun getExchangeRates(
        apiKey: String,
        symbols: String,
        base: String
    ): ExchangeRatesResponseX {
        return  exchanges
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
        return checkoutSession
    }

    override suspend fun getCustomerOrders(userId: Long): OrderResponse {
        return orders
    }

    override suspend fun createOrder(order: Map<String, OrderBody>): OrderBodyResponse {
        return orderResult
    }

    override suspend fun getSingleOrder(orderId: Long): OrderResponse {
       return orders
    }
}