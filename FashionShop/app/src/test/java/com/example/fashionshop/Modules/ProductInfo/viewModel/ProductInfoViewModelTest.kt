package com.example.fashionshop.Modules.ProductInfo.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.ProductDetails
import com.example.fashionshop.Model.ProductImage
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.Repository.FakeRepository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ProductInfoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: ProductInfoViewModel
    private lateinit var productDetails: ProductDetails
    private val listId = 1L

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = ProductInfoViewModel(repository, listId)

        productDetails = ProductDetails(
            1,
            "Product 1",
            "Product 1 body",
            ProductImage("image3.png"),
            listOf(ProductImage("image3.png")),
            listOf(Variant("300")),
            "Brand 1")
        repository.products.add(ProductResponse(product = productDetails))
    }

    @Test
    fun getProductInfo_ReturnSameProduct() = runBlockingTest {
        viewModel.getProductInfo(productDetails.id!!)

        var result = ProductResponse()

        val job = launch {
            viewModel.product.collectLatest { state ->
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

        assertThat(result.product, not(nullValue()))
        assertThat(result.product, IsEqual(productDetails))
    }
}