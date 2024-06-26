package com.example.fashionshop.Modules.Orders.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.Address
import com.example.fashionshop.Model.LineItemBody
import com.example.fashionshop.Model.Order
import com.example.fashionshop.Model.OrderCurrency
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Repository.FakeRepository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class OrdersViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = OrdersViewModel(repository)
    }

    @Test
    fun getOrders_success() = runBlockingTest {
        // Mock data setup
        val mockOrders = listOf(
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

        // Set up repository response

        // Call the function under test
        viewModel.getOrders(userId = 1)

        // Delay to ensure the coroutine completes
        kotlinx.coroutines.delay(100)

        // Collect the states emitted by the ViewModel
        val result = viewModel.orders.first()

        // Assert that the result is of type NetworkState.Success
        assertThat(result, CoreMatchers.instanceOf(NetworkState.Success::class.java))

        // Assert that the data inside NetworkState.Success matches the expected data
        assertThat((result as NetworkState.Success).data.orders, IsEqual(mockOrders))
    }
}
