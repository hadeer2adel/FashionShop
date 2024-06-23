package com.example.fashionshop.Modules.Category.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductImage
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.Repository.FakeRepository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CategoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: CategoryViewModel

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = CategoryViewModel(repository)
    }

    @Test
    fun getProducts_success() = runTest {
        val mockProducts = listOf(
            Product(
                id = 1,
                title = "Nike Air Max",
                image = ProductImage(src = "https://example.com/nike_air_max.jpg"),
                variants = listOf(
                    Variant(price = "$100", inventory_quantity = 10),
                    Variant(price = "$120", inventory_quantity = 5)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            ),
            Product(
                id = 2,
                title = "Adidas Ultraboost",
                image = ProductImage(src = "https://example.com/adidas_ultraboost.jpg"),
                variants = listOf(
                    Variant(price = "$150", inventory_quantity = 8),
                    Variant(price = "$160", inventory_quantity = 3)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            ),
            Product(
                id = 3,
                title = "Nike Dri-FIT T-shirt",
                image = ProductImage(src = "https://example.com/nike_tshirt.jpg"),
                variants = listOf(
                    Variant(price = "$30", inventory_quantity = 20),
                    Variant(price = "$35", inventory_quantity = 15)
                ),
                tags = "Sportswear, T-shirt",
                product_type = "Clothing"
            ),
            Product(
                id = 4,
                title = "Adidas Originals Hoodie",
                image = ProductImage(src = "https://example.com/adidas_hoodie.jpg"),
                variants = listOf(
                    Variant(price = "$70", inventory_quantity = 12),
                    Variant(price = "$75", inventory_quantity = 10)
                ),
                tags = "Sportswear, Hoodie",
                product_type = "Clothing"
            )
        )
        val mockProductResponse = ProductResponse(products = mockProducts)

        viewModel.getProducts()

        var result: NetworkState<ProductResponse>? = null
        val job = launch {
            viewModel.products.collectLatest { state ->
                result = state
            }
        }

        advanceUntilIdle()

        job.cancelAndJoin()

        assertThat(result, CoreMatchers.instanceOf(NetworkState.Success::class.java))
        assertThat((result as NetworkState.Success).data, IsEqual(mockProductResponse))
    }

    @Test
    fun filterProducts_success() = runTest {
        val mockProducts = listOf(
            Product(
                id = 1,
                title = "Nike Air Max",
                image = ProductImage(src = "https://example.com/nike_air_max.jpg"),
                variants = listOf(
                    Variant(price = "$100", inventory_quantity = 10),
                    Variant(price = "$120", inventory_quantity = 5)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            ),
            Product(
                id = 2,
                title = "Adidas Ultraboost",
                image = ProductImage(src = "https://example.com/adidas_ultraboost.jpg"),
                variants = listOf(
                    Variant(price = "$150", inventory_quantity = 8),
                    Variant(price = "$160", inventory_quantity = 3)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            ),
            Product(
                id = 3,
                title = "Nike Dri-FIT T-shirt",
                image = ProductImage(src = "https://example.com/nike_dri_fit.jpg"),
                variants = listOf(
                    Variant(price = "$30", inventory_quantity = 20)
                ),
                tags = "Sportswear, T-shirt",
                product_type = "Clothing"
            )
        )

        val mockProductResponse = ProductResponse(products = mockProducts)

        viewModel.getProducts()
        advanceUntilIdle()

        var initialResult: NetworkState<ProductResponse>? = null
        val initialJob = launch {
            viewModel.products.collectLatest { state ->
                initialResult = state
            }
        }

        advanceUntilIdle()
        initialJob.cancel()

        println("Initial Products: ${(initialResult as? NetworkState.Success)?.data?.products}")

        viewModel.filterProducts("Running", "Shoes")

        var result: NetworkState<ProductResponse>? = null
        val job = launch {
            viewModel.products.collectLatest { state ->
                result = state
            }
        }

        advanceUntilIdle()

        job.cancelAndJoin()

        val expectedFilteredProducts = listOf(
            Product(
                id = 1,
                title = "Nike Air Max",
                image = ProductImage(src = "https://example.com/nike_air_max.jpg"),
                variants = listOf(
                    Variant(price = "$100", inventory_quantity = 10),
                    Variant(price = "$120", inventory_quantity = 5)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            ),
            Product(
                id = 2,
                title = "Adidas Ultraboost",
                image = ProductImage(src = "https://example.com/adidas_ultraboost.jpg"),
                variants = listOf(
                    Variant(price = "$150", inventory_quantity = 8),
                    Variant(price = "$160", inventory_quantity = 3)
                ),
                tags = "Running, Sneakers",
                product_type = "Shoes"
            )
        )

        println("Filtered Products: ${(result as? NetworkState.Success)?.data?.products}")
        assertThat(result, CoreMatchers.instanceOf(NetworkState.Success::class.java))
        assertThat((result as NetworkState.Success).data.products, IsEqual(expectedFilteredProducts))
    }





}
