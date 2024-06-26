package com.example.fashionshop.Modules.Authentication.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Repository.FakeRepository
import com.example.fashionshop.Service.Networking.NetworkState
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

@RunWith(AndroidJUnit4::class)
class AuthenticationViewModelTest2 {

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var firstCustomer: CustomerResponse.Customer

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = AuthenticationViewModel(repository)
        firstCustomer = CustomerResponse.Customer( 1L, "First", "Customer", "first@gmail.com", "", "EGY", 0L,  0L)
        repository.customers.add(firstCustomer)
    }

    @Test
    fun updateCustomer_NoteAndMultipassIdentifierNotZero() = runBlockingTest {
        viewModel.updateCustomer(firstCustomer.id)

        var result = CustomerResponse()

        val job = launch {
            viewModel.customer.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> { }
                    is NetworkState.Success ->{
                        result = response.data
                    }
                    is NetworkState.Failure ->{  }
                }
            }
        }

        advanceUntilIdle()
        job.cancelAndJoin()

        MatcherAssert.assertThat(result.customer , CoreMatchers.not(CoreMatchers.nullValue()))
        MatcherAssert.assertThat(result.customer?.id, IsEqual(firstCustomer.id))
        MatcherAssert.assertThat(result.customer?.note, CoreMatchers.not(IsEqual(0L)))
        MatcherAssert.assertThat(result.customer?.multipass_identifier, CoreMatchers.not(IsEqual(0L)))
    }

}