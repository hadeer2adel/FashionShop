package com.example.fashionshop.Modules.Address.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.Customer
import com.example.fashionshop.Model.DefaultAddress
import com.example.fashionshop.Model.EmailMarketingConsent
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.SmsMarketingConsent
import com.example.fashionshop.Modules.Payment.viewModel.PaymentViewModel
import com.example.fashionshop.Repository.FakeRepository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
    ))
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddressViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: AddressViewModel
    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = AddressViewModel(repository)
    }
    @Test
    fun getAllcustomer() = runBlockingTest {
        viewModel.getAllcustomer(123456)
        var result: OneCustomer? = customer
        val job = launch {
            viewModel.products.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> {}
                    is NetworkState.Success -> {
                        result = state.data
                    }
                    is NetworkState.Failure -> {}
                }
            }
        }

        advanceUntilIdle()
        job.cancelAndJoin()

        MatcherAssert.assertThat(result, CoreMatchers.not(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result!!.customer.email, IsEqual(customer.customer.email))
        MatcherAssert.assertThat(result!!.customer.currency, IsEqual(customer.customer.currency))
    }
    @Test
    fun senddeleteAddressRequest() = runBlockingTest {
        viewModel.senddeleteAddressRequest(12345,1234567)


    }


}