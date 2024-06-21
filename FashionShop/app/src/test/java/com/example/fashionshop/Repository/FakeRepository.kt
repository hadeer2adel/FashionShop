package com.example.fashionshop.Repository

import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.Customer
import com.example.fashionshop.Model.CustomerAddress
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.InvoiceCreation
import com.example.fashionshop.Model.InvoiceData
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.OrderBodyResponse
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Model.PaymentMethodOptions
import com.example.fashionshop.Model.PhoneNumberCollection
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.TotalDetails
import com.example.fashionshop.Model.UpdateCustomerRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeRepository :Repository {
    var addressList : MutableList<AddressRequest> = mutableListOf()
    var pricesCodes : MutableList<PriceRule> = mutableListOf()
    var checkoutList :MutableList<CheckoutSessionResponse> = mutableListOf()
    var customer :MutableList<OneCustomer> = mutableListOf()
    override suspend fun getcustomers(id: Long): OneCustomer {
        return customer[0]
    }

    override suspend fun createCustomer(customer: CustomerRequest): CustomerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getBrands(): Response<BrandResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getBrandProducts(vendor: String): Response<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomerByEmail(email: String): CustomerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun AddSingleCustomerAdreess(
        id: Long,
        addressRequest: AddressRequest
    ): AddressRequest {
        addressList.add(addressRequest)
        return addressRequest
    }


    override suspend fun editSingleCustomerAddress(
        customerID: Long,
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
        val address = addressList.find { it.address.customer_id == customerID && it.address.id == id }
        address?.let {
            val updatedAddress = it.address.copy(default = addressRequest.address.default)
            addressList[addressList.indexOf(it)] = AddressRequest(updatedAddress)
            return AddressUpdateRequest(
                CustomerAddress(
                    address1 = updatedAddress.address1,
                    address2 = updatedAddress.address2.toString(),
                    city = updatedAddress.city,
                    company = updatedAddress.company.toString(),
                    country = updatedAddress.country,
                    country_code = updatedAddress.country_code,
                    country_name = updatedAddress.country_name,
                    customer_id = updatedAddress.customer_id,
                    default = updatedAddress.default,
                    first_name = updatedAddress.first_name,
                    id = updatedAddress.id,
                    last_name = updatedAddress.last_name,
                    name = updatedAddress.name,
                    phone = updatedAddress.phone,
                    province = updatedAddress.province.toString(),
                    province_code = updatedAddress.province_code.toString(),
                    zip = updatedAddress.zip
                )
            )
        } ?: throw IllegalArgumentException("Address not found")
    }

    override suspend fun deleteSingleCustomerAddress(customerID: Long, id: Long) {
        val address = addressList.find { it.address.customer_id == customerID && it.address.id == id }
        address?.let { addressList.remove(it) }
    }

    override suspend fun getCustomerOrders(userId: Long): Response<OrderResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getDiscountCodes(): Flow<PriceRule> = flow {
        pricesCodes.forEach { emit(it) }
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
//        val expectedCheckoutSession = CheckoutSessionResponse(
//            id = "cs_test_a1vMqMBnX2DyP93CEed8rmSKRqQyTUlMTvepNQp4MttdYOd4ilRWbON9ug",
//            object = "checkout.session",
//            after_expiration = null,
//            allow_promotion_codes = null,
//            amount_subtotal = 50000,
//            amount_total = 50000,
//            automatic_tax = AutomaticTax(false, null, null),
//            billing_address_collection = null,
//            cancel_url = cancelUrl,
//            client_reference_id = null,
//            client_secret = null,
//            consent = null,
//            consent_collection = null,
//            created = 1718714694,
//            currency = currency,
//            currency_conversion = null,
//            custom_fields = emptyList(),
//            custom_text = CustomText(null, null, null, null),
//            customer = null,
//            customer_creation = "if_required",
//            customer_details = CustomerDetails(null, customerEmail, null, null, "none", null),
//            customer_email = customerEmail,
//            expires_at = 1718801094,
//            id = "cs_test_a1vMqMBnX2DyP93CEed8rmSKRqQyTUlMTvepNQp4MttdYOd4ilRWbON9ug",
//            invoice = null,
//            invoice_creation = InvoiceCreation(false, InvoiceData(null, null, null, null, null, null)),
//            livemode = false,
//            locale = null,
//            metadata = emptyMap(),
//            mode = mode,
//            `object` = "checkout.session",
//            payment_intent = null,
//            payment_link = null,
//            payment_method_collection = "if_required",
//            payment_method_configuration_details = null,
//            payment_method_options = PaymentMethodOptions(CardOptions("automatic")),
//            payment_method_types = listOf("card"),
//            payment_status = "unpaid",
//            phone_number_collection = PhoneNumberCollection(false),
//            recovered_from = null,
//            saved_payment_method_options = null,
//            setup_intent = null,
//            shipping_address_collection = null,
//            shipping_cost = null,
//            shipping_details = null,
//            shipping_options = emptyList(),
//            status = "open",
//            submit_type = null,
//            subscription = null,
//            success_url = successUrl,
//            total_details = TotalDetails(0, 0, 0),
//            ui_mode = "hosted",
//            url = "https://checkout.stripe.com/c/pay/cs_test_a1vMqMBnX2DyP93CEed8rmSKRqQyTUlMTvepNQp4MttdYOd4ilRWbON9ug#fidkdWxOYHwnPyd1blpxYHZxWjA0VVZnUWJBamBcS1ZmZ1FoZE5QSVxMdnBUNjZXa0p9dH1yamBXN0ZBPXVsS3dkYm1tcUh0TUxtRHZuQXBwdGpAdEZ9VTNWZnRqVXVtN2N9VENjYzRdb3N9NTVfSWJVaXBCPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"
//        )
//
//        checkoutList.add(checkoutSession)
//        return checkoutSession
        TODO("Not yet implemented")

    }

    override suspend fun createOrder(order: Map<String, OrderBody>): OrderBodyResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getSingleOrder(orderId: Long): Response<OrderResponse> {
        TODO("Not yet implemented")
    }
}