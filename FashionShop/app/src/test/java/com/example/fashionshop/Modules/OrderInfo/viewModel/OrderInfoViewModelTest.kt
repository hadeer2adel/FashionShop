package com.example.fashionshop.Modules.Home.viewModel

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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class OrderInfoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: OrderInfoViewModel

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = OrderInfoViewModel(repository)
    }

    @Test
    fun getOrder_success() = runBlockingTest {
        // Mock data setup
        val expectedOrder = Order(
            id = 1,
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
            number = 1,
            order_number = 1001,
            order_status_url = "https://example.com/orders/1", // Update to use actual order ID
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
                line_items = emptyList()
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
                line_items = emptyList()
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


        // Call the function under test
        viewModel.getOrder(userId = 1)

        // Delay to ensure the coroutine completes
        kotlinx.coroutines.delay(100)

        // Collect the states emitted by the ViewModel
        val result = viewModel.order.first()

        // Assert that the result is of type NetworkState.Success
        assertThat(result, CoreMatchers.instanceOf(NetworkState.Success::class.java))

        // Assert that the data inside NetworkState.Success matches the expected data
        assertThat((result as NetworkState.Success).data.order, IsEqual(expectedOrder))
    }

}
