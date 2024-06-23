package com.example.fashionshop.Modules.Home.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.BrandImage
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.PrerequisiteQuantityRange
import com.example.fashionshop.Model.PrerequisiteToEntitlementPurchase
import com.example.fashionshop.Model.PrerequisiteToEntitlementQuantityRatio
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleX
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Repository.FakeRepository
import com.example.fashionshop.Service.Networking.NetworkState
import com.ibm.icu.impl.Assert.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun getBrands_success() = runTest {
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
        val mockBrandResponse = BrandResponse(smart_collections = brands)

        viewModel.getBrands()

        var result: NetworkState<BrandResponse>? = null
        val job = launch {
            viewModel.brand.collectLatest { state ->
                result = state
            }
        }

        advanceUntilIdle()

        job.cancelAndJoin()

        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(NetworkState.Success::class.java))
        MatcherAssert.assertThat((result as NetworkState.Success).data, IsEqual(mockBrandResponse))
    }

//    @Test
//    fun getAdsCode_success() = runTest {
//        // Mock successful response from repository
//        val mockPriceRule = PriceRule(
//            price_rules = listOf(
//                PriceRuleX(
//                    admin_graphql_api_id = "",
//                    allocation_limit = "null",
//                    allocation_method = "",
//                    created_at = "",
//                    customer_segment_prerequisite_ids = emptyList(),
//                    customer_selection = "",
//                    ends_at = "",
//                    entitled_collection_ids = emptyList(),
//                    entitled_country_ids = emptyList(),
//                    entitled_product_ids = emptyList(),
//                    entitled_variant_ids = emptyList(),
//                    id = 1L,
//                    once_per_customer = false,
//                    prerequisite_collection_ids = emptyList(),
//                    prerequisite_customer_ids = emptyList(),
//                    prerequisite_product_ids = emptyList(),
//                    prerequisite_quantity_range = PrerequisiteQuantityRange(2),
//                    prerequisite_shipping_price_range = "null",
//                    prerequisite_subtotal_range = "null",
//                    prerequisite_to_entitlement_purchase = PrerequisiteToEntitlementPurchase(3),
//                    prerequisite_to_entitlement_quantity_ratio = PrerequisiteToEntitlementQuantityRatio(3, 3),
//                    prerequisite_variant_ids = emptyList(),
//                    starts_at = "",
//                    target_selection = "",
//                    target_type = "",
//                    title = "",
//                    updated_at = "'",
//                    usage_limit = "",
//                    value = "",
//                    value_type = ""
//                )
//            )
//        )
//
//        // Trigger the method under test
//        viewModel.getAdsCode()
//
//        var result: NetworkState<PriceRule>? = null
//        val job = launch {
//            viewModel.products.collectLatest { state ->
//                result = state
//            }
//        }
//
//        // Advance time to ensure the asynchronous operation completes
//        advanceUntilIdle()
//
//        // Cancel the coroutine job to clean up
//        job.cancelAndJoin()
//
//        // Assert that the result is a NetworkState.Success with expected data
//        assertThat(result, CoreMatchers.instanceOf(NetworkState.Success::class.java))
//
//        // Check if result is actually a success state before accessing data
//        if (result is NetworkState.Success) {
//            assertThat((result as NetworkState.Success<PriceRule>).data, CoreMatchers.equalTo(mockPriceRule))
//        } else {
//            fail("Expected NetworkState.Success but was ${result?.javaClass?.simpleName}")
//        }
//    }





}