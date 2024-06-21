import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fashionshop.Model.*
import com.example.fashionshop.Modules.Authentication.viewModel.AuthenticationViewModel
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(AndroidJUnit4::class)
class AuthenticationViewModelTest {

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
    fun createCustomer_CustomerResponseSameAsCustomerRequest() = runBlockingTest {
        val customerRequest = CustomerRequest(CustomerRequest.Customer(
            "Hadeer",
            "Adel",
            "hadeer@gmail.com"
        ))

        viewModel.createCustomer(customerRequest)

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

        assertThat(result.customer , not(nullValue()))
        assertThat(result.customer?.first_name, IsEqual(customerRequest.customer.first_name))
        assertThat(result.customer?.last_name, IsEqual(customerRequest.customer.last_name))
        assertThat(result.customer?.email, IsEqual(customerRequest.customer.email))
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

        assertThat(result.customer , not(nullValue()))
        assertThat(result.customer?.id, IsEqual(firstCustomer.id))
        assertThat(result.customer?.note, not(IsEqual(0L)))
        assertThat(result.customer?.multipass_identifier, not(IsEqual(0L)))
    }

    @Test
    fun getCustomerByEmail_success() = runBlockingTest {
        val email = firstCustomer.email

        viewModel.getCustomerByEmail(email)

        var result = CustomerResponse()

        val job = launch {
            viewModel.customers.collectLatest { response ->
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

        assertThat(result.customers , not(nullValue()))
        assertThat(result.customers?.first()?.email, IsEqual(email))
    }

    @Test
    fun getCustomerByEmail_fail() = runBlockingTest {
        val email = "second@gmail.com"

        viewModel.getCustomerByEmail(email)

        var result = CustomerResponse()

        val job = launch {
            viewModel.customers.collectLatest { response ->
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

        assertThat(result.customers , nullValue())
    }

}

