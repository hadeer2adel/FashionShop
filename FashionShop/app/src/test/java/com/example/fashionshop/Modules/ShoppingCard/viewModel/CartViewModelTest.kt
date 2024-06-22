package com.example.fashionshop.Modules.ShoppingCard.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductImage
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
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
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CartViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: CartViewModel
    private lateinit var product1: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var product2: DraftOrderResponse.DraftOrder.LineItem
    private lateinit var CartList: DraftOrderResponse.DraftOrder
    private val listId = 1L

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = CartViewModel(repository, listId)

        product1 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 1, "Product 1", "100", "1*image1.png")
        product2 = DraftOrderResponse.DraftOrder.LineItem(null, 1, 2, "Product 2", "200", "2*image2.png")
        CartList = DraftOrderResponse.DraftOrder(line_items = listOf(product1, product2))
        val draftOrderResponse = DraftOrderResponse(CartList)
        repository.draftOrders.add(draftOrderResponse)
    }

    @Test
    fun getCardProducts_ResultSameListAsCartList() = runBlockingTest {

        viewModel.getCardProducts()

        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val job = launch {
            viewModel.productCard.collectLatest { state ->
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


        MatcherAssert.assertThat(result.draft_order , CoreMatchers.not(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result.draft_order, IsEqual(CartList))
    }

//    @Test
//    fun insertFavProduct_LineItemsListIncreasedByOne() = runBlockingTest {
//        val product = Product(3L, "Product 3", ProductImage("image3.png"), listOf(Variant("300")))
//
//        viewModel.insertFavProduct(product)
//
//
//        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())
//
//        val job = launch {
//            viewModel.product.collectLatest { state ->
//                when (state) {
//                    is NetworkState.Loading -> { }
//                    is NetworkState.Success -> {
//                        result = state.data
//                    }
//                    is NetworkState.Failure -> { }
//                }
//            }
//        }
//
//        advanceUntilIdle()
//        job.cancelAndJoin()
//
//
//        MatcherAssert.assertThat(result.draft_order , CoreMatchers.not(CoreMatchers.nullValue()))
//        MatcherAssert.assertThat(result.draft_order.line_items.size, IsEqual(CartList.line_items.size + 1))
//    }

    @Test
    fun deleteCardProduct_LineItemsListDecreasedByOne() = runBlockingTest {

        viewModel.deleteCardProduct(product1.id!!)

        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val job = launch {
            viewModel.productCard.collectLatest { state ->
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


        MatcherAssert.assertThat(result.draft_order , CoreMatchers.not(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result.draft_order.line_items.size, IsEqual(CartList.line_items.size - 1))
        MatcherAssert.assertThat(result.draft_order.line_items.first(), IsEqual(product2))

    }
    @Test
    fun editCardQuantityProduct_UpdatesProductQuantityAndPrice() = runBlockingTest {
        val updatedQuantity = 5
        val updatedPrice = "150"

        viewModel.editCardQuantityProduct(product1.id!!, updatedQuantity, updatedPrice)

        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val job = launch {
            viewModel.productCard.collectLatest { state ->
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

        MatcherAssert.assertThat(result.draft_order, CoreMatchers.not(CoreMatchers.nullValue()))
        val updatedProduct = result.draft_order.line_items.first { it.id == product1.id }
        MatcherAssert.assertThat(updatedProduct.quantity, IsEqual(updatedQuantity))
        MatcherAssert.assertThat(updatedProduct.price, IsEqual(updatedPrice))
    }
    @Test
    fun deleteAllCartProducts_EmptiesCartExceptFirstItem() = runBlockingTest {
        viewModel.deleteAllCartProducts()

        var result = DraftOrderResponse(DraftOrderResponse.DraftOrder())

        val job = launch {
            viewModel.productCard.collectLatest { state ->
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

        MatcherAssert.assertThat(result.draft_order, CoreMatchers.not(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result.draft_order.line_items.size, IsEqual(1))
    }
}