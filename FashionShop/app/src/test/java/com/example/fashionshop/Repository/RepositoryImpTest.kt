package com.example.fashionshop.Repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fashionshop.Model.Address
import com.example.fashionshop.Model.AddressBody
import com.example.fashionshop.Model.AddressDefault
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.AutomaticTax
import com.example.fashionshop.Model.BrandImage
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.Card
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.CustomText
import com.example.fashionshop.Model.Customer
import com.example.fashionshop.Model.CustomerAddress
import com.example.fashionshop.Model.CustomerBody
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.CustomerDetails
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.DefaultAddress
import com.example.fashionshop.Model.DefaultAddressBody
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.EmailMarketingConsent
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.InvoiceCreation
import com.example.fashionshop.Model.InvoiceData
import com.example.fashionshop.Model.LineItemBody
import com.example.fashionshop.Model.MetadataX
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.Order
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.OrderBodyResponse
import com.example.fashionshop.Model.OrderCurrency
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Model.PaymentMethodOptions
import com.example.fashionshop.Model.PhoneNumberCollection
import com.example.fashionshop.Model.PrerequisiteQuantityRange
import com.example.fashionshop.Model.PrerequisiteToEntitlementPurchase
import com.example.fashionshop.Model.PrerequisiteToEntitlementQuantityRatio
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleX
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductDetails
import com.example.fashionshop.Model.ProductImage
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.RatesX
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Model.SmsMarketingConsent
import com.example.fashionshop.Model.TotalDetails
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.Service.Networking.FakeNetworkManager
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import kotlin.random.Random


class RepositoryImpTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    lateinit var  fakeRemote : NetworkManager
    lateinit var  repository :Repository
    var address =AddressRequest(
        Addresse(
            address1 = "123 Main St",
            address2 = "Apt 101",
            city = "Springfield",
            company = "ABC Inc.",
            country = "USA",
            country_code = "US",
            country_name = "United States",
            customer_id = 1L,
            default = true,
            first_name = "John",
            id = 1L,
            last_name = "Doe",
            name = "John Doe",
            phone = "+123456789",
            province = "State",
            province_code = "ST",
            zip = "12345"
        )
    )
    var updatedAddress = AddressUpdateRequest(
        CustomerAddress(
            address1 = "",
            address2 = "",
            city = "",
            company = "",
            country = "",
            country_code = "",
            country_name = "",
            customer_id = 0L,
            default = false,
            first_name = "",
            id = 0L,
            last_name = "",
            name = "",
            phone = "",
            province = "",
            province_code = "",
            zip = ""
        )
    )
    val addressDefault = AddressDefultRequest(AddressDefault(default = false))
    var prices = PriceRule(
        price_rules = listOf(
            PriceRuleX(
                admin_graphql_api_id = "",
                allocation_limit = "null",
                allocation_method = "",
                created_at = "",
                customer_segment_prerequisite_ids = emptyList(),
                customer_selection = "",
                ends_at = "",
                entitled_collection_ids = emptyList(),
                entitled_country_ids = emptyList(),
                entitled_product_ids = emptyList(),
                entitled_variant_ids = emptyList(),
                id = 0L,
                once_per_customer = false,
                prerequisite_collection_ids = emptyList(),
                prerequisite_customer_ids = emptyList(),
                prerequisite_product_ids = emptyList(),
                prerequisite_quantity_range = PrerequisiteQuantityRange(2),
                prerequisite_shipping_price_range = "null",
                prerequisite_subtotal_range = "null",
                prerequisite_to_entitlement_purchase = PrerequisiteToEntitlementPurchase(3),
                prerequisite_to_entitlement_quantity_ratio = PrerequisiteToEntitlementQuantityRatio(3,3),
                prerequisite_variant_ids = emptyList(),
                starts_at = "",
                target_selection = "",
                target_type = "",
                title = "",
                updated_at = "'",
                usage_limit = "",
                value = "",
                value_type = ""
            )
        )
    )
    var exchanges = ExchangeRatesResponseX("USD","", RatesX(39.00),true,123)
    var customer = OneCustomer(
       Customer(addresses = emptyList(),
    admin_graphql_api_id = "admin_id",
    created_at = "2024-06-21",
    currency = "USD",
    default_address = DefaultAddress( address1 = "",
        address2 = "",
        city = "",
        company = "",
        country = "",
        country_code = "",
        country_name = "",
        customer_id = 0L,
        default = false,
        first_name = "",
        id = 0L,
        last_name = "",
        name = "",
        phone = "",
        province = "",
        province_code = "",
        zip = ""),
    email = "customer@example.com",
    email_marketing_consent = EmailMarketingConsent("","",""),
    first_name = "John",
    id = 7371713577180,
    last_name = "Doe",
    last_order_id = 123456,
    last_order_name = "Order #123456",
    multipass_identifier = "null",
    note = "null",
    orders_count = 3,
    phone = "+1234567890",
    sms_marketing_consent = SmsMarketingConsent("","","",""),
    state = "active",
    tags = "VIP",
    tax_exempt = false,
    tax_exemptions = emptyList(),
    total_spent = "1000.00",
    updated_at = "2024-06-21",
    verified_email = true
    )

    )
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

    val brands = BrandResponse(
        listOf(
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
    )

    val products = ProductResponse(
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
    )

    val orders = OrderResponse(
        listOf(
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
        )

        // Add more orders as needed
    )

    val addressOrder = AddressBody(
        first_name = "ahmed",
        address1 = "",
        phone = "12345",
        city = "city",
        zip = "zip",
        country = "country",
        last_name = "last_name",
        name = "name",
        country_code = "country_code"
    )


    val defaultAddressOrder = DefaultAddressBody(
        first_name = "ahmed",
        address1 = "",
        phone = "12345",
        city = "city",
        zip = "zip",
        country = "country",
        last_name = "last_name",
        name = "name",
        country_code = "country_code",
        default = true
    )

    val customerOrder = CustomerBody(
        id = 123L,
        email = "ahmedkh2711@gmail.com",
        first_name = "Ahmed",
        last_name = "Khaled",
        currency = "currency",
        default_address = defaultAddressOrder
    )

    val lineItemOrder = LineItemBody(
        variant_id = 124L,
        quantity = 200,
        id = 147L,
        title = "title",
        price = "price",
        sku = "sku"

    )
    val lineItemsList = listOf(lineItemOrder)

    val orderBody = OrderBody(
        billing_address = addressOrder,
        customer = customerOrder,
        line_items = lineItemsList,
        total_tax = 13.5,
        currency ="EGY",
        total_discounts = "25" ,
        referring_site = "cash"
    )
    val orderBodyResponse = OrderBodyResponse(orderBody)
    val wrappedOrderBody = mapOf("order" to orderBody)

    private lateinit var firstCustomer: CustomerResponse.Customer
    private var customers = mutableListOf<CustomerResponse.Customer>()
    private var draftOrders = mutableListOf<DraftOrderResponse>()
    private lateinit var product1: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var product2: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var favList: DraftOrderResponse.DraftOrder
    private val listId = 1L
    private lateinit var productDetails: ProductDetails
    private var productsList = mutableListOf<ProductResponse>()

    @Before
    fun setup() {
        firstCustomer = CustomerResponse.Customer( 1L, "First", "Customer", "first@gmail.com", "", "EGY", 0L,  0L)
        customers.add(firstCustomer)
        product1 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 1, "Product 1", "100", "1*image1.png")
        product2 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 2, "Product 2", "200", "2*image2.png")
        favList = DraftOrderResponse.DraftOrder(line_items = listOf(product1, product2))
        draftOrders.add(DraftOrderResponse(favList))

        productDetails = ProductDetails(
            1,
            "Product 1",
            "Product 1 body",
            ProductImage("image3.png"),
            listOf(ProductImage("image3.png")),
            listOf(Variant("300")),
            "Brand 1")
        productsList.add(ProductResponse(product = productDetails))

        fakeRemote = FakeNetworkManager(address,updatedAddress,prices,customer,checkout,exchanges, customers, draftOrders , brands , products , orders , orderBodyResponse, productsList)
        repository=RepositoryImp(fakeRemote)
    }
    @Test
    fun getcustomers() = runBlockingTest {
        val result=repository.getcustomers(7371713577180)
        MatcherAssert.assertThat(result.customer.id, CoreMatchers.`is`(7371713577180))
    }


    @Test
    fun createCustomer_CustomerResponseSameAsCustomerRequest() = runBlockingTest {
        //Given
        val customerRequest = CustomerRequest(
            CustomerRequest.Customer(
                "Hadeer",
                "Adel",
                "hadeer@gmail.com"
            ))

        //When
        val result = repository.createCustomer(customerRequest)

        //Then
        assertThat(result.first().customer, not(nullValue()))
        assertThat(result.first().customer?.first_name, IsEqual(customerRequest.customer.first_name))
        assertThat(result.first().customer?.last_name, IsEqual(customerRequest.customer.last_name))
        assertThat(result.first().customer?.email, IsEqual(customerRequest.customer.email)) }
    @Test
    fun getBrands() = runBlockingTest {
        val result = repository.getBrands()
        assertThat(result, not(nullValue()))
        assertThat(result?.first()?.smart_collections?.size, `is`(2))
        assertThat(result?.first()?.smart_collections?.get(0)?.title, `is`("Nike"))
    }
    @Test
    fun getBrandProducts() = runBlockingTest {
        val result = repository.getBrandProducts("nike")
        assertThat(result, not(nullValue()))
        result.first().products
        assertThat(result.first().products?.size, `is`(equalTo(2))) // Assuming two products for Nike
        assertThat(result.first().products?.get(0)?.title, `is`("Nike Air Max"))
        assertThat(result.first().products?.get(1)?.title, `is`("Nike Dri-FIT T-shirt"))
        assertThat(result.first().products?.get(0)?.variants?.size, `is`(equalTo(2))) // Assuming two variants for Nike Air Max
        assertThat(result.first().products?.get(1)?.variants?.size, `is`(equalTo(2))) // Assuming two variants for Nike Dri-FIT T-shirt
    }
    @Test
    fun getProducts() = runBlockingTest {
        val result = repository.getProducts()
        // Assert
        assertThat(result, not(nullValue()))
        assertThat(result.first().products?.size, `is`(equalTo(2))) // Assuming two products are returned
        assertThat(result.first().products?.get(0)?.title, `is`("Nike Air Max"))
        assertThat(result.first().products?.get(1)?.title, `is`("Nike Dri-FIT T-shirt"))
    }

    @Test
    fun getCustomerByEmail_success() = runBlockingTest {
        val email = firstCustomer.email
        var result = repository.getCustomerByEmail(email)

        assertThat(result.first().customers , not(nullValue()))
        assertThat(result.first().customers?.first()?.email, IsEqual(email)) }

    @Test
    fun getCustomerByEmail_fail() = runBlockingTest {
        val email = "second@gmail.com"
        var result = repository.getCustomerByEmail(email)

        assertThat(result.first().customers, nullValue())
        assertThat(result.first().customers?.first()?.email, not(IsEqual(email))) }

    @Test
    fun addSingleCustomerAdreess()= runBlockingTest{
        val result=repository.AddSingleCustomerAdreess(7371713577180,address)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(address))
        MatcherAssert.assertThat(result.address.address1, CoreMatchers.`is`(address.address.address1))
    }
    @Test
    fun editSingleCustomerAddress() = runBlockingTest {
        val result=repository.editSingleCustomerAddress(7371713577180,12345,addressDefault)
        MatcherAssert.assertThat(result.customer_address.default, CoreMatchers.`is`(addressDefault.address.default))
    }
    @Test
    fun deleteSingleCustomerAddress() = runBlockingTest{
        repository.deleteSingleCustomerAddress(7371713577180,1234)
        val result=repository.getcustomers(7371713577180)
        MatcherAssert.assertThat(result.customer.addresses.size, CoreMatchers.`is`(0))
    }

    @Test
    fun getCustomerOrders() = runBlockingTest {
        // Given
        val userId = 1L // Assuming userId for testing

        // When
        val flow = repository.getCustomerOrders(userId)
        val result = flow.first() // Collect the first emission from the flow

        // Then
        assertThat(result, not(nullValue())) // Ensure the result is not null

        // Validate specific properties of the first order in the response
        assertThat(result.orders?.size, equalTo(1)) // Assuming only one order is returned
        val order = result.orders?.first()
        assertThat(order?.id, equalTo(1))
        assertThat(order?.email, equalTo("user@example.com"))
        assertThat(order?.total_price, equalTo("90.00"))

        // Validate billing address
        assertThat(order?.billing_address, not(nullValue()))
        assertThat(order?.billing_address?.first_name, equalTo("John"))
        assertThat(order?.billing_address?.last_name, equalTo("Doe"))
        assertThat(order?.billing_address?.address1, equalTo("123 Main St"))
        assertThat(order?.billing_address?.phone, equalTo("+1234567890"))
        assertThat(order?.billing_address?.city, equalTo("New York"))
        assertThat(order?.billing_address?.zip, equalTo("10001"))
        assertThat(order?.billing_address?.province, equalTo("NY"))
        assertThat(order?.billing_address?.country, equalTo("US"))

        // Validate shipping address
        assertThat(order?.shipping_address, not(nullValue()))
        assertThat(order?.shipping_address?.first_name, equalTo("Jane"))
        assertThat(order?.shipping_address?.last_name, equalTo("Smith"))
        assertThat(order?.shipping_address?.address1, equalTo("456 Elm St"))
        assertThat(order?.shipping_address?.phone, equalTo("+1234567890"))
        assertThat(order?.shipping_address?.city, equalTo("Los Angeles"))
        assertThat(order?.shipping_address?.zip, equalTo("90001"))
        assertThat(order?.shipping_address?.province, equalTo("CA"))
        assertThat(order?.shipping_address?.country, equalTo("US"))

        // Validate line items
        assertThat(order?.line_items, not(nullValue()))
        assertThat(order?.line_items?.size, equalTo(1)) // Assuming one line item in the order
        val lineItem = order?.line_items?.first()
        assertThat(lineItem?.id, equalTo(1))
        assertThat(lineItem?.title, equalTo("Nike Air Max"))
        assertThat(lineItem?.quantity, equalTo(1))
        assertThat(lineItem?.price, equalTo("90.00"))
        assertThat(lineItem?.sku, equalTo("NM123"))
        assertThat(lineItem?.variant_id, equalTo(123456789))
    }

    @Test
    fun getDiscountCodes()= runBlockingTest {
        val result=repository.getDiscountCodes()
        MatcherAssert.assertThat(result.first(), CoreMatchers.`is`(prices))
    }

    @Test
    fun getProductInfo_ReturnSameProduct() = runBlockingTest {
        var result = repository.getProductById(productDetails.id!!)

        assertThat(result.first().product, not(nullValue()))
        assertThat(result.first().product, IsEqual(productDetails)) }
    
    @Test
    fun createDraftOrders_IdNotZero() = runBlockingTest {
        val draftOrderResponse = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val result = repository.createDraftOrders(draftOrderResponse)

        assertThat(result.first().draft_order , not(nullValue()))
        assertThat(result.first().draft_order.id, not(IsEqual(0L)))}

    @Test
    fun updateDraftOrder_LineItemsListIncreasedByOne() = runBlockingTest {
        val product3 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 3, "Product 3", "300", "3*image3.png")
        val draftOrder = draftOrders.first().draft_order

        val updatedLineItems = draftOrder.line_items.toMutableList().apply {
            add(product3)
        }
        val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)

        var result = repository.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))

        assertThat(result.first().draft_order , not(nullValue()))
        assertThat(result.first().draft_order.line_items.size, IsEqual(favList.line_items.size + 1)) }

    @Test
    fun getDraftOrder_ResultSameListAsFavList() = runBlockingTest {
        var result = repository.getDraftOrder(listId)

        assertThat(result.first().draft_order , not(nullValue()))
        assertThat(result.first().draft_order, IsEqual(favList)) }

    @Test
    fun updateCustomer_NoteAndMultipassIdentifierNotZero() = runBlockingTest {
        val favListId = Random.nextLong(1L, 10000L)
        val cartId = Random.nextLong(1L, 10000L)
        val updateCustomerRequest = UpdateCustomerRequest(
            UpdateCustomerRequest.Customer(favListId, cartId)
        )

        var result = repository.updateCustomer(firstCustomer.id, updateCustomerRequest)

        assertThat(result.first().customer , not(nullValue()))
        assertThat(result.first().customer?.id, IsEqual(firstCustomer.id))
        assertThat(result.first().customer?.note, not(IsEqual(0L)))
        assertThat(result.first().customer?.note, IsEqual(favListId))
        assertThat(result.first().customer?.multipass_identifier, not(IsEqual(0L)))
        assertThat(result.first().customer?.multipass_identifier, IsEqual(cartId)) }

    @Test
    fun createOrder_OrderResponseSameAsOrderRequest() = runBlockingTest {
        //When
        val result = repository.createOrder(wrappedOrderBody)

        //Then
        assertThat(result.first().order, not(nullValue()))
        assertThat(result.first().order?.customer?.first_name, IsEqual(orderBody.customer.first_name))
        assertThat(result.first().order?.customer?.last_name, IsEqual(orderBody.customer.last_name))
        assertThat(result.first().order?.customer?.email, IsEqual(orderBody.customer.email))
    }

    @Test
    fun getSingleOrder()= runBlockingTest {

        // When
        val flow = repository.getSingleOrder(123)
        val result = flow.first() // Collect the first emission from the flow

        // Then
        assertThat(result, not(nullValue())) // Ensure the result is not null

        // Validate specific properties of the first order in the response
        assertThat(result.orders?.size, equalTo(1)) // Assuming only one order is returned
        val order = result.orders?.first()
        assertThat(order?.id, equalTo(1))
        assertThat(order?.email, equalTo("user@example.com"))
        assertThat(order?.total_price, equalTo("90.00"))

        // Validate billing address
        assertThat(order?.billing_address, not(nullValue()))
        assertThat(order?.billing_address?.first_name, equalTo("John"))
        assertThat(order?.billing_address?.last_name, equalTo("Doe"))
        assertThat(order?.billing_address?.address1, equalTo("123 Main St"))
        assertThat(order?.billing_address?.phone, equalTo("+1234567890"))
        assertThat(order?.billing_address?.city, equalTo("New York"))
        assertThat(order?.billing_address?.zip, equalTo("10001"))
        assertThat(order?.billing_address?.province, equalTo("NY"))
        assertThat(order?.billing_address?.country, equalTo("US"))

        // Validate shipping address
        assertThat(order?.shipping_address, not(nullValue()))
        assertThat(order?.shipping_address?.first_name, equalTo("Jane"))
        assertThat(order?.shipping_address?.last_name, equalTo("Smith"))
        assertThat(order?.shipping_address?.address1, equalTo("456 Elm St"))
        assertThat(order?.shipping_address?.phone, equalTo("+1234567890"))
        assertThat(order?.shipping_address?.city, equalTo("Los Angeles"))
        assertThat(order?.shipping_address?.zip, equalTo("90001"))
        assertThat(order?.shipping_address?.province, equalTo("CA"))
        assertThat(order?.shipping_address?.country, equalTo("US"))

        // Validate line items
        assertThat(order?.line_items, not(nullValue()))
        assertThat(order?.line_items?.size, equalTo(1)) // Assuming one line item in the order
        val lineItem = order?.line_items?.first()
        assertThat(lineItem?.id, equalTo(1))
        assertThat(lineItem?.title, equalTo("Nike Air Max"))
        assertThat(lineItem?.quantity, equalTo(1))
        assertThat(lineItem?.price, equalTo("90.00"))
        assertThat(lineItem?.sku, equalTo("NM123"))
        assertThat(lineItem?.variant_id, equalTo(123456789))
    }

    @Test
    fun getExchangeRates() = runBlockingTest{
        val result = repository.getExchangeRates("tVEqdM1Lv5eZwifx7UyalZFZ4svWWsHo","EGP","USD")
        MatcherAssert.assertThat(result.base,CoreMatchers.`is`("USD"))
    }
    @Test
    fun createCheckoutSession()= runBlockingTest {
        val result=repository.createCheckoutSession("https://example.com/cancel","https://example.com/cancel","user@gmail.com","USD","Your Order","Please Write your Card Information",
            10000,1,"payment","card")
        MatcherAssert.assertThat(result.cancel_url, CoreMatchers.`is`("https://example.com/cancel"))
        MatcherAssert.assertThat(result.success_url, CoreMatchers.`is`("https://example.com/cancel"))
        MatcherAssert.assertThat(result.mode, CoreMatchers.`is`("payment"))


    }
}