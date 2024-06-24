package com.example.fashionshop.Modules.OrderDetails.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.*
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
class OrderDetailsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: OrderDetailsViewModel
    private val orderBody = OrderBody(
        billing_address = AddressBody("user","address1","address2","cairo","12345","Egypt","user","cairo","12345"),
        customer = CustomerBody(12345, "user@gmail.com", "user", "user", "USD", DefaultAddressBody("user","address1","address2","cairo","12345","Egypt","user","cairo","12345",true)
        ),
        line_items = listOf(LineItemBody(123456, 2, 123456, "dummy", "22", "")),
        total_tax = 13.5,
        currency = "USD"
    )
    private val priceRule = PriceRule(
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
                prerequisite_to_entitlement_quantity_ratio = PrerequisiteToEntitlementQuantityRatio(3, 3),
                prerequisite_variant_ids = emptyList(),
                starts_at = "",
                target_selection = "",
                target_type = "",
                title = "Discount Code",
                updated_at = "'",
                usage_limit = "",
                value = "",
                value_type = ""
            )
        )
    )
    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = OrderDetailsViewModel(repository)
        repository.pricesCodes.add(priceRule)
        repository.createdOrders.add(OrderBodyResponse(orderBody))
    }
    @Test
    fun getAdsCode_Success() = runBlockingTest {
        viewModel.getAdsCode()

        var result: PriceRule =priceRule
        val job = launch {
            viewModel.productCode.collectLatest { state ->
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
        MatcherAssert.assertThat(result!!.price_rules[0].title, IsEqual("Discount Code"))
    }


    @Test
    fun createOrder_Success() = runBlockingTest {
        val orderBodyMap = mapOf("order" to orderBody)
        var orderCreated = false
        var errorMessage: String? = null

        viewModel.createOrder(orderBodyMap, {
            orderCreated = true
        }, { error ->
            errorMessage = error
        })

        var result: OrderBodyResponse? = OrderBodyResponse(orderBody)
        val job = launch {
            viewModel.order.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> {
                        // Handle loading state if needed
                    }
                    is NetworkState.Success -> {
                        result = state.data
                    }
                    is NetworkState.Failure -> {
                        // Handle failure state if needed
                    }
                }
            }
        }

        advanceUntilIdle()
        job.cancelAndJoin()
        MatcherAssert.assertThat(orderCreated, CoreMatchers.equalTo(true))
        MatcherAssert.assertThat(errorMessage, CoreMatchers.nullValue())
        MatcherAssert.assertThat(result, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(result!!.order!!.currency, IsEqual("USD"))
    }
}
