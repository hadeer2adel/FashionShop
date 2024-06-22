package com.example.fashionshop.Repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fashionshop.Model.AddressDefault
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.AutomaticTax
import com.example.fashionshop.Model.Card
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.CustomText
import com.example.fashionshop.Model.Customer
import com.example.fashionshop.Model.CustomerAddress
import com.example.fashionshop.Model.CustomerDetails
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.DefaultAddress
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.EmailMarketingConsent
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.InvoiceCreation
import com.example.fashionshop.Model.InvoiceData
import com.example.fashionshop.Model.MetadataX
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.PaymentMethodOptions
import com.example.fashionshop.Model.PhoneNumberCollection
import com.example.fashionshop.Model.PrerequisiteQuantityRange
import com.example.fashionshop.Model.PrerequisiteToEntitlementPurchase
import com.example.fashionshop.Model.PrerequisiteToEntitlementQuantityRatio
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleX
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductImage
import com.example.fashionshop.Model.RatesX
import com.example.fashionshop.Model.SmsMarketingConsent
import com.example.fashionshop.Model.TotalDetails
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.Service.Networking.FakeNetworkManager
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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

    private lateinit var firstCustomer: CustomerResponse.Customer
    private var customers = mutableListOf<CustomerResponse.Customer>()
    private var draftOrders = mutableListOf<DraftOrderResponse>()
    private lateinit var product1: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var product2: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var favList: DraftOrderResponse.DraftOrder
    private val listId = 1L

    @Before
    fun setup() {
        firstCustomer = CustomerResponse.Customer( 1L, "First", "Customer", "first@gmail.com", "", "EGY", 0L,  0L)
        customers.add(firstCustomer)
        product1 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 1, "Product 1", "100", "1*image1.png")
        product2 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 2, "Product 2", "200", "2*image2.png")
        favList = DraftOrderResponse.DraftOrder(line_items = listOf(product1, product2))
        draftOrders.add(DraftOrderResponse(favList))

        fakeRemote = FakeNetworkManager(address,updatedAddress,prices,customer,checkout,exchanges, customers, draftOrders)
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

    fun getBrands() {
    }

    fun getBrandProducts() {
    }

    fun getProducts() {
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

    fun getCustomerOrders() {
    }
    @Test
    fun getDiscountCodes()= runBlockingTest {
        val result=repository.getDiscountCodes()
        MatcherAssert.assertThat(result.first(), CoreMatchers.`is`(prices))
    }

    fun getProductById() {
    }

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

    fun createOrder() {
    }

    fun getSingleOrder() {
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