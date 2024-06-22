package com.example.fashionshop.Modules.Payment.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.AutomaticTax
import com.example.fashionshop.Model.Card
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.CustomText
import com.example.fashionshop.Model.CustomerDetails
import com.example.fashionshop.Model.InvoiceCreation
import com.example.fashionshop.Model.InvoiceData
import com.example.fashionshop.Model.MetadataX
import com.example.fashionshop.Model.PaymentMethodOptions
import com.example.fashionshop.Model.PhoneNumberCollection
import com.example.fashionshop.Model.TotalDetails
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PaymentViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: PaymentViewModel

    val successUrl = "https://example.com/success"
    val cancelUrl = "https://example.com/cancel"
    val customerEmail = "user@gmail.com"
    val currency = "USD"
    val productName = "Test Product"
    val productDescription = "Test Product Description"
    val unitAmountDecimal = 50000
    val quantity = 1
    val mode = "payment"
    val paymentMethodType = "card"
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
        metadata = MetadataX(),
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
    fun setUp() {
        repository = FakeRepository()
        viewModel = PaymentViewModel(repository)
    }

    @Test
    fun getPaymentProducts_Success() = runBlockingTest {
        viewModel.getPaymentProducts(
            successUrl,
            cancelUrl,
            customerEmail,
            currency,
            productName,
            productDescription,
            unitAmountDecimal,
            quantity,
            mode,
            paymentMethodType
        )

        var result: CheckoutSessionResponse? = checkout
        val job = launch {
            viewModel.productPayment.collectLatest { state ->
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
        MatcherAssert.assertThat(result!!.customer_email, IsEqual(customerEmail))
        MatcherAssert.assertThat(result!!.currency, IsEqual(currency))
        MatcherAssert.assertThat(result!!.amount_total, IsEqual(unitAmountDecimal * quantity))
    }
}
