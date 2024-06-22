package com.example.fashionshop.Modules.Address.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.Addresse
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
class AddNewAddressViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: AddNewAddressViewModel

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = AddNewAddressViewModel(repository)
    }

    @Test
    fun addSingleCustomerAddress() = runBlockingTest {
        val customerId = 123456L
        val address = Addresse(
            address1 = "123 Main St",
            address2 = "Apt 4",
            city = "Anytown",
            company = "Company",
            country = "Country",
            country_code = "CC",
            country_name = "Country Name",
            customer_id = customerId,
            default = true,
            first_name = "John",
            id = 1L,
            last_name = "Doe",
            name = "John Doe",
            phone = "+1234567890",
            province = "Province",
            province_code = "PC",
            zip = "12345"
        )
        val addressRequest = AddressRequest(address)

        viewModel.addSingleCustomerAddress(customerId, addressRequest)

        var result: AddressRequest? = addressRequest
        val job = launch {
            viewModel.addressRequestResult.collectLatest { state ->
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
        MatcherAssert.assertThat(result!!.address.address1, IsEqual("123 Main St"))
        MatcherAssert.assertThat(result!!.address.city, IsEqual("Anytown"))
    }
}
