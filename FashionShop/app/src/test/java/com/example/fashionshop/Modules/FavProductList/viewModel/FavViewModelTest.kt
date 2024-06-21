package com.example.fashionshop.Modules.FavProductList.viewModel

import FakeRepository
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductImage
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.R
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: FavViewModel
    private lateinit var product1: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var product2: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var favList: DraftOrderResponse.DraftOrder
    private val listId = 1L

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = FavViewModel(repository, listId)

        product1 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 1, "Product 1", "100", "1*image1.png")
        product2 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 2, "Product 2", "200", "2*image2.png")
        favList = DraftOrderResponse.DraftOrder(line_items = listOf(product1, product2))
        val draftOrderResponse = DraftOrderResponse(favList)
        repository.draftOrders.add(draftOrderResponse)
    }

    @Test
    fun getFavProducts_ResultSameListAsFavList() = runBlockingTest {

        viewModel.getFavProducts()

        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val job = launch {
            viewModel.product.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> { }
                    is NetworkState.Success -> {
                        result = state.data
                    }
                    is NetworkState.Failure -> { }
                }
            }
        }

        advanceUntilIdle()
        job.cancelAndJoin()


        assertThat(result.draft_order , not(nullValue()))
        assertThat(result.draft_order, IsEqual(favList))
    }

    @Test
    fun isFavProduct_true() = runBlockingTest {
        viewModel.isFavProduct(product1.id!!, { assertTrue(true) }, { assertTrue(false) })
        advanceUntilIdle()
    }

    @Test
    fun isFavProduct_false() = runBlockingTest {
        viewModel.isFavProduct(5, { assertFalse(true) }, { assertFalse(false) })
        advanceUntilIdle()
    }

    @Test
    fun insertFavProduct_LineItemsListIncreasedByOne() = runBlockingTest {
        val product = Product(3L, "Product 3", ProductImage("image3.png"), listOf(Variant("300")))

        viewModel.insertFavProduct(product)


        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val job = launch {
            viewModel.product.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> { }
                    is NetworkState.Success -> {
                        result = state.data
                    }
                    is NetworkState.Failure -> { }
                }
            }
        }

        advanceUntilIdle()
        job.cancelAndJoin()


        assertThat(result.draft_order , not(nullValue()))
        assertThat(result.draft_order.line_items.size, IsEqual(favList.line_items.size + 1))
    }

    @Test
    fun deleteFavProduct_LineItemsListDecreasedByOne() = runBlockingTest {

        viewModel.deleteFavProduct(product1.id!!)

        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val job = launch {
            viewModel.product.collectLatest { state ->
                when (state) {
                    is NetworkState.Loading -> { }
                    is NetworkState.Success -> {
                        result = state.data
                    }
                    is NetworkState.Failure -> { }
                }
            }
        }

        advanceUntilIdle()
        job.cancelAndJoin()


        assertThat(result.draft_order , not(nullValue()))
        assertThat(result.draft_order.line_items.size, IsEqual(favList.line_items.size - 1))
        assertThat(result.draft_order.line_items.first(), IsEqual(product2))

    }
}
