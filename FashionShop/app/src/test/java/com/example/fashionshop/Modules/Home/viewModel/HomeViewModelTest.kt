package com.example.fashionshop.Modules.Home.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.BrandImage
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Repository.FakeRepository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
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

}