package com.example.fashionshop.Repository

import com.example.fashionshop.Model.*
import retrofit2.Response
import kotlin.random.Random
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.CustomerAddress
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date

class FakeRepository :Repository {
    var addressList : MutableList<AddressRequest> = mutableListOf()
    var pricesCodes : MutableList<PriceRule> = mutableListOf()
    var checkoutList :MutableList<CheckoutSessionResponse> = mutableListOf()
    var customer :MutableList<OneCustomer> = mutableListOf()

    var shouldReturnError = false
    var customers = mutableListOf<CustomerResponse.Customer>()
    var draftOrders = mutableListOf<DraftOrderResponse>()
    var createdOrders = mutableListOf<OrderBodyResponse>()
    var  exchenges = mutableListOf<ExchangeRatesResponseX>()
    var products = mutableListOf<ProductResponse>()
    override suspend fun getcustomers(id: Long): OneCustomer {
        return customer[0]
    }

    override suspend fun createCustomer(customerRequest: CustomerRequest): Flow<CustomerResponse> {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
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

        return flowOf(CustomerResponse(customer))
    }

    override suspend fun getBrands(): Flow<BrandResponse> {
        val brands = listOf(
            SmartCollection(
                id = 1,
                title = "Nike",
                image = BrandImage(
                    src = "https://example.com/nike.jpg"
                )
            ),
            SmartCollection(
                id = 2,
                title = "Adidas",
                image = BrandImage(
                    src = "https://example.com/adidas.jpg"
                )
            )
        )
        val brandResponse = BrandResponse(smart_collections = brands)
        return flowOf(brandResponse)
    }

    override suspend fun getBrandProducts(vendor: String): Flow<ProductResponse> {
        // Generate fake product data for demonstration
        val products = when (vendor.toLowerCase()) {
            "nike" -> {
                listOf(
                    Product(
                        id = 1,
                        title = "Nike Air Max",
                        image = ProductImage(src = "https://example.com/nike_air_max.jpg"),
                        variants = listOf(
                            Variant(price = "$100", inventory_quantity = 10),
                            Variant(price = "$120", inventory_quantity = 5)
                        ),
                        tags = "Running, Sneakers",
                        product_type = "Shoes"
                    ),
                    Product(
                        id = 2,
                        title = "Nike Dri-FIT T-shirt",
                        image = ProductImage(src = "https://example.com/nike_tshirt.jpg"),
                        variants = listOf(
                            Variant(price = "$30", inventory_quantity = 20),
                            Variant(price = "$35", inventory_quantity = 15)
                        ),
                        tags = "Sportswear, T-shirt",
                        product_type = "Clothing"
                    )
                )
            }
            "adidas" -> {
                listOf(
                    Product(
                        id = 3,
                        title = "Adidas Ultraboost",
                        image = ProductImage(src = "https://example.com/adidas_ultraboost.jpg"),
                        variants = listOf(
                            Variant(price = "$150", inventory_quantity = 8),
                            Variant(price = "$160", inventory_quantity = 3)
                        ),
                        tags = "Running, Sneakers",
                        product_type = "Shoes"
                    ),
                    Product(
                        id = 4,
                        title = "Adidas Originals Hoodie",
                        image = ProductImage(src = "https://example.com/adidas_hoodie.jpg"),
                        variants = listOf(
                            Variant(price = "$70", inventory_quantity = 12),
                            Variant(price = "$75", inventory_quantity = 10)
                        ),
                        tags = "Sportswear, Hoodie",
                        product_type = "Clothing"
                    )
                )
            }
            else -> emptyList()  // Handle unknown vendors
        }
        val productResponse = ProductResponse(products = products)

        return flowOf(productResponse)
    }

    override suspend fun getProducts(): Flow<ProductResponse> {
        val products = listOf(
            Product(
                id = 1,
                title = "Nike Air Max",
                image = ProductImage(src = "https://example.com/nike_air_max.jpg"),
                variants = listOf(
                    Variant(price = "$100", inventory_quantity = 10),
                    Variant(price = "$120", inventory_quantity = 5)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            ),
            Product(
                id = 2,
                title = "Adidas Ultraboost",
                image = ProductImage(src = "https://example.com/adidas_ultraboost.jpg"),
                variants = listOf(
                    Variant(price = "$150", inventory_quantity = 8),
                    Variant(price = "$160", inventory_quantity = 3)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            ),
            Product(
                id = 3,
                title = "Nike Dri-FIT T-shirt",
                image = ProductImage(src = "https://example.com/nike_tshirt.jpg"),
                variants = listOf(
                    Variant(price = "$30", inventory_quantity = 20),
                    Variant(price = "$35", inventory_quantity = 15)
                ),
                tags = "Sportswear, T-shirt",
                product_type = "Clothing"
            ),
            Product(
                id = 4,
                title = "Adidas Originals Hoodie",
                image = ProductImage(src = "https://example.com/adidas_hoodie.jpg"),
                variants = listOf(
                    Variant(price = "$70", inventory_quantity = 12),
                    Variant(price = "$75", inventory_quantity = 10)
                ),
                tags = "Sportswear, Hoodie",
                product_type = "Clothing"
            )
        )

        val productResponse = ProductResponse(products = products)

        return flowOf(productResponse)
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

    override suspend fun getCustomerOrders(userId: Long): Flow<OrderResponse> {
        val orders = listOf(
            Order(
                id = 1,
                admin_graphql_api_id = "gid://shopify/Order/123456789",
                browser_ip = "127.0.0.1",
                buyer_accepts_marketing = true,
                cart_token = "abcdefghijk123456789",
                checkout_id = 123456789,
                checkout_token = "xyz987654321",
                confirmed = true,
                contact_email = "user@example.com",
                created_at = Date().toString(),
                currency = OrderCurrency.Usd,
                current_subtotal_price = "100.00",
                current_total_discounts = "10.00",
                current_total_price = "90.00",
                current_total_tax = "5.00",
                email = "user@example.com",
                estimated_taxes = false,
                financial_status = "paid",
                landing_site = "https://example.com",
                landing_site_ref = "123",
                number = 123,
                order_number = 456,
                order_status_url = "https://example.com/orders/123",
                payment_gateway_names = listOf("visa", "mastercard"),
                phone = "+1234567890",
                po_number = "PO123",
                presentment_currency = OrderCurrency.Usd,
                processed_at = Date().toString(),
                reference = "ref123",
                referring_site = "https://example.com/referrer",
                source_identifier = "web",
                source_name = "web",
                subtotal_price = "90.00",
                tags = "important, urgent",
                taxExempt = false,
                test = false,
                token = "token123",
                total_discounts = "10.00",
                total_line_items_price = "100.00",
                total_outstanding = "90.00",
                total_price = "90.00",
                total_tax = "5.00",
                total_tip_received = "0.00",
                total_weight = 2000,
                updated_at = Date().toString(),
                billing_address = Address(
                    first_name = "John",
                    address1 = "123 Main St",
                    phone = "+1234567890",
                    city = "New York",
                    zip = "10001",
                    province = "NY",
                    country = "US",
                    last_name = "Doe",
                    address2 = "Apt 1",
                    latitude = 40.7128,
                    longitude = -74.0060,
                    name = "John Doe",
                    country_code = "US",
                    province_code = "NY",
                    id = 1,
                    customer_id = 1,
                    country_name = "United States",
                    default = true
                ),
                shipping_address = Address(
                    first_name = "Jane",
                    address1 = "456 Elm St",
                    phone = "+1234567890",
                    city = "Los Angeles",
                    zip = "90001",
                    province = "CA",
                    country = "US",
                    last_name = "Smith",
                    address2 = "Suite 2",
                    latitude = 34.0522,
                    longitude = -118.2437,
                    name = "Jane Smith",
                    country_code = "US",
                    province_code = "CA",
                    id = 2,
                    customer_id = 1,
                    country_name = "United States",
                    default = false
                ),
                line_items = listOf(
                    LineItemBody(
                        id = 1,
                        title = "Nike Air Max",
                        quantity = 1,
                        price = "90.00",
                        sku = "NM123",
                        variant_id = 123456789,
                    )
                )
            )
            // Add more orders as needed
        )

        val orderResponse = OrderResponse(orders = orders)

        return flowOf(orderResponse)
    }


    override suspend fun getDiscountCodes(): Flow<PriceRule> = flow {
        pricesCodes.forEach { emit(it) }
    }

    override suspend fun getProductById(id: Long): Flow<ProductResponse> {
        return flowOf(products.get((id - 1).toInt())) }

    override suspend fun createDraftOrders(draftOrderResponse: DraftOrderResponse): Flow<DraftOrderResponse> {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        val id = Random.nextLong(1L, 10000L)
        val fakeDraftOrderResponse = DraftOrderResponse(DraftOrderResponse.DraftOrder(id = id))
        return flowOf(fakeDraftOrderResponse)
    }

    override suspend fun updateDraftOrder(
        id: Long,
        draftOrder: DraftOrderResponse
    ): Flow<DraftOrderResponse> {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        draftOrders.set((id - 1).toInt(), draftOrder)
        return flowOf(draftOrders.get((id - 1).toInt()))
    }

    override suspend fun getDraftOrder(id: Long): Flow<DraftOrderResponse> {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        return flowOf(draftOrders.get((id - 1).toInt()))
    }

    override suspend fun updateCustomer(id: Long, updateCustomerRequest: UpdateCustomerRequest): Flow<CustomerResponse> {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        var customer = customers.get(id.toInt() - 1)
        customer.note = updateCustomerRequest.customer.note!!
        customer.multipass_identifier = updateCustomerRequest.customer.multipass_identifier!!
        return flowOf(CustomerResponse(customer))
    }

    override suspend fun getExchangeRates(
        apiKey: String,
        symbols: String,
        base: String
    ): ExchangeRatesResponseX {
        return exchenges[0]
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
        var checkout = CheckoutSessionResponse(
            after_expiration = "null",
            allow_promotion_codes = "null",
            amount_subtotal = 50000,
            amount_total = 50000,
            automatic_tax = AutomaticTax(false, "null", "null"),
            billing_address_collection = "null",
            cancel_url = "https://example.com/cancel",
            client_reference_id = "null",
            client_secret = "null",
            consent = "null",
            consent_collection = "null",
            created = 1718714694,
            currency = "USD",
            currency_conversion = "null",
            custom_fields = emptyList(),
            custom_text = CustomText("null", "null", "null", "null"),
            customer = "null",
            customer_creation = "if_required",
            customer_details = CustomerDetails("null", "customerEmail", "null", "null", "none", "null"),
            customer_email = "user@gmail.com",
            expires_at = 1718801094,
            id = "cs_test_a1vMqMBnX2DyP93CEed8rmSKRqQyTUlMTvepNQp4MttdYOd4ilRWbON9ug",
            invoice = "null",
            invoice_creation = InvoiceCreation(false, InvoiceData("null", "null", "null", "null", "null", MetadataX(),"null")),
            livemode = false,
            locale = "null",
            metadata =MetadataX(),
            mode = "payment",
            `object` = "checkout.session",
            payment_intent = "null",
            payment_link = "null",
            payment_method_collection = "if_required",
            payment_method_configuration_details = "null",
            payment_method_options = PaymentMethodOptions(Card("")),
            payment_method_types = listOf("card"),
            payment_status = "unpaid",
            phone_number_collection = PhoneNumberCollection(false),
            recovered_from = "null",
            saved_payment_method_options = "null",
            setup_intent = "null",
            shipping_address_collection = "null",
            shipping_cost = "null",
            shipping_details = "null",
            shipping_options = emptyList(),
            status = "open",
            submit_type = "null",
            subscription = "null",
            success_url = "https://example.com/cancel",
            total_details = TotalDetails(0, 0, 0),
            ui_mode = "hosted",
            url = "https://checkout.stripe.com/c/pay/cs_test_a1vMqMBnX2DyP93CEed8rmSKRqQyTUlMTvepNQp4MttdYOd4ilRWbON9ug#fidkdWxOYHwnPyd1blpxYHZxWjA0VVZnUWJBamBcS1ZmZ1FoZE5QSVxMdnBUNjZXa0p9dH1yamBXN0ZBPXVsS3dkYm1tcUh0TUxtRHZuQXBwdGpAdEZ9VTNWZnRqVXVtN2N9VENjYzRdb3N9NTVfSWJVaXBCPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"
        )
       checkoutList.add(checkout)
        return checkout

    }

    override suspend fun createOrder(order: Map<String, OrderBody>): Flow<OrderBodyResponse> {
        val orderBodyResponse = OrderBodyResponse(
            order = order.values.firstOrNull()
        )
        return flowOf(orderBodyResponse)
    }

    override suspend fun getSingleOrder(orderId: Long): Flow<OrderResponse> {
        val order = Order(
            id = orderId,
            admin_graphql_api_id = "dummy_api_id",
            browser_ip = "127.0.0.1",
            buyer_accepts_marketing = true,
            cart_token = "dummy_cart_token",
            checkout_id = 1,
            checkout_token = "dummy_checkout_token",
            confirmed = true,
            contact_email = "user@example.com",
            created_at = "2024-06-22T10:15:30Z",
            currency = OrderCurrency.Usd,
            current_subtotal_price = "100.00",
            current_total_discounts = "10.00",
            current_total_price = "90.00",
            current_total_tax = "5.00",
            email = "user@example.com",
            estimated_taxes = true,
            financial_status = "paid",
            landing_site = "https://example.com",
            landing_site_ref = "referral_site",
            number = orderId,
            order_number = 1001,
            order_status_url = "https://example.com/orders/$orderId",
            payment_gateway_names = listOf("stripe"),
            phone = "1234567890",
            po_number = "PO123456",
            presentment_currency = OrderCurrency.Usd,
            processed_at = "2024-06-22T10:30:45Z",
            reference = "reference123",
            referring_site = "referral_site",
            source_identifier = "source_id",
            source_name = "web",
            subtotal_price = "100.00",
            tags = "tag1,tag2",
            taxExempt = false,
            test = false,
            token = "token123",
            total_discounts = "10.00",
            total_line_items_price = "90.00",
            total_outstanding = "0.00",
            total_price = "95.00",
            total_tax = "5.00",
            total_tip_received = "0.00",
            total_weight = 1500,
            updated_at = "2024-06-22T11:00:00Z",
            billing_address = Address(
                first_name = "John",
                address1 = "123 Main St",
                phone = "1234567890",
                city = "Anytown",
                zip = "12345",
                province = "Province",
                country = "Country",
                last_name = "Doe",
                address2 = "Apt 1",
                latitude = 37.7749,
                longitude = -122.4194,
                name = "John Doe",
                country_code = "US",
                province_code = "CA",
                id = 1,
                customer_id = 1,
                country_name = "United States",
                default = true,
                line_items = emptyList() // Adjust with actual line items if needed
            ),
            shipping_address = Address(
                first_name = "Jane",
                address1 = "456 Elm St",
                phone = "9876543210",
                city = "Othertown",
                zip = "54321",
                province = "Province",
                country = "Country",
                last_name = "Smith",
                address2 = "",
                latitude = 37.7749,
                longitude = -122.4194,
                name = "Jane Smith",
                country_code = "US",
                province_code = "CA",
                id = 2,
                customer_id = 2,
                country_name = "United States",
                default = false,
                line_items = emptyList() // Adjust with actual line items if needed
            ),
            line_items = listOf(
                LineItemBody(
                    variant_id = 1,
                    quantity = 2,
                    id = 1,
                    title = "Product A",
                    price = "50.00",
                    sku = "SKU-001"
                ),
                LineItemBody(
                    variant_id = 2,
                    quantity = 1,
                    id = 2,
                    title = "Product B",
                    price = "40.00",
                    sku = "SKU-002"
                )
            )
        )

        val orderResponse = OrderResponse(order = order)
        return flowOf(orderResponse)
    }

    override suspend fun getCustomerByEmail(email: String): Flow<CustomerResponse> {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        customers.forEach{ customer ->  
            if (customer.email.equals(email)){
                return flowOf(CustomerResponse(customers = listOf(customer)))
            }
        }
        return flowOf(CustomerResponse())
    }
}
