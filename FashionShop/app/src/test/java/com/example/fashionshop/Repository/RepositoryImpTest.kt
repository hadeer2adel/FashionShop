package com.example.fashionshop.Repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.fashionshop.Model.AddressDefault
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.CustomerAddress
import com.example.fashionshop.Model.PrerequisiteQuantityRange
import com.example.fashionshop.Model.PrerequisiteToEntitlementPurchase
import com.example.fashionshop.Model.PrerequisiteToEntitlementQuantityRatio
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleX
import com.example.fashionshop.Service.Networking.FakeNetworkManager
import com.example.fashionshop.Service.Networking.NetworkManager
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
    @Before
    fun setup() {
        fakeRemote = FakeNetworkManager(address,updatedAddress,prices)
        repo=RepositoryImp(fakeRemote)
    }
    fun getcustomers() {
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

    fun deleteSingleCustomerAddress() {
    }

    fun getCustomerOrders() {
    }

    fun getDiscountCodes() {
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

    fun getExchangeRates() {
    }

    fun createCheckoutSession() {
    }
}