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
import com.example.fashionshop.Model.DefaultAddress
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
import com.example.fashionshop.Model.RatesX
import com.example.fashionshop.Model.SmsMarketingConsent
import com.example.fashionshop.Model.TotalDetails
import com.example.fashionshop.Service.Networking.FakeNetworkManager
import com.example.fashionshop.Service.Networking.NetworkManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class RepositoryImpTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    lateinit var  fakeRemote : NetworkManager
    lateinit var  repo :Repository
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
    @Before
    fun setup() {
        fakeRemote = FakeNetworkManager(address,updatedAddress,prices,customer,checkout,exchanges)
        repo=RepositoryImp(fakeRemote)
    }
    @Test
    fun getcustomers() = runBlockingTest {
        val result=repo.getcustomers(7371713577180)
        MatcherAssert.assertThat(result.customer.id, CoreMatchers.`is`(7371713577180))
    }

    fun createCustomer() {
    }

    fun getBrands() {
    }

    fun getBrandProducts() {
    }

    fun getProducts() {
    }

    fun getCustomerByEmail() {
    }
    @Test
    fun addSingleCustomerAdreess()= runBlockingTest{
        val result=repo.AddSingleCustomerAdreess(7371713577180,address)
        MatcherAssert.assertThat(result, CoreMatchers.`is`(address))
        MatcherAssert.assertThat(result.address.address1, CoreMatchers.`is`(address.address.address1))
    }
    @Test
    fun editSingleCustomerAddress() = runBlockingTest {
        val result=repo.editSingleCustomerAddress(7371713577180,12345,addressDefault)
        MatcherAssert.assertThat(result.customer_address.default, CoreMatchers.`is`(addressDefault.address.default))
    }
    @Test
    fun deleteSingleCustomerAddress() = runBlockingTest{
        repo.deleteSingleCustomerAddress(7371713577180,1234)
        val result=repo.getcustomers(7371713577180)
        MatcherAssert.assertThat(result.customer.addresses.size, CoreMatchers.`is`(0))
    }

    fun getCustomerOrders() {
    }
    @Test
    fun getDiscountCodes()= runBlockingTest {
        val result=repo.getDiscountCodes()
        MatcherAssert.assertThat(result.first(), CoreMatchers.`is`(prices))
    }

    fun getProductById() {
    }

    fun createDraftOrders() {
    }

    fun updateDraftOrder() {
    }

    fun getDraftOrder() {
    }

    fun updateCustomer() {
    }

    fun createOrder() {
    }

    fun getSingleOrder() {
    }
    @Test
    fun getExchangeRates() = runBlockingTest{
        val result = repo.getExchangeRates("tVEqdM1Lv5eZwifx7UyalZFZ4svWWsHo","EGP","USD")
        MatcherAssert.assertThat(result.base,CoreMatchers.`is`("USD"))
    }
    @Test
    fun createCheckoutSession()= runBlockingTest {
        val result=repo.createCheckoutSession("https://example.com/cancel","https://example.com/cancel","user@gmail.com","USD","Your Order","Please Write your Card Information",
            10000,1,"payment","card")
        MatcherAssert.assertThat(result.cancel_url, CoreMatchers.`is`("https://example.com/cancel"))
        MatcherAssert.assertThat(result.success_url, CoreMatchers.`is`("https://example.com/cancel"))
        MatcherAssert.assertThat(result.mode, CoreMatchers.`is`("payment"))


    }
}