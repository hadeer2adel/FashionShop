package com.example.fashionshop.Modules.Authentication.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.Customer
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthenticationViewModel(private var repository: Repository) : ViewModel() {

    private var _customers = MutableStateFlow<NetworkState<CustomerResponse>>(NetworkState.Loading)
    var customers: StateFlow<NetworkState<CustomerResponse>> = _customers

    private var _customer = MutableStateFlow<NetworkState<CustomerResponse>>(NetworkState.Loading)
    var customer: StateFlow<NetworkState<CustomerResponse>> = _customer

    fun createCustomer(customer: CustomerRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createCustomer(customer)
                .catch {_customer.value = NetworkState.Failure(it) }
                .collect {updateCustomer(it.customer!!.id) }
        }
    }

    fun updateCustomer(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val draftOrderResponse = DraftOrderResponse(DraftOrderResponse.DraftOrder())
            var favListId = 0L
            var cartId = 0L

            repository.createDraftOrders(draftOrderResponse)
                .catch {_customer.value = NetworkState.Failure(it)}
                .collect { favListId = it.draft_order.id }
            repository.createDraftOrders(draftOrderResponse)
                .catch {_customer.value = NetworkState.Failure(it)}
                .collect { cartId = it.draft_order.id }

            val updateCustomerRequest = UpdateCustomerRequest(
                UpdateCustomerRequest.Customer(favListId, cartId)
            )
            repository.updateCustomer(id, updateCustomerRequest)
                .catch {_customer.value = NetworkState.Failure(it)}
                .collect {_customer.value = NetworkState.Success(it)}
        }
    }

    fun getCustomerByEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCustomerByEmail(email)
                .catch { _customers.value = NetworkState.Failure(it) }
                .collect { _customers.value = NetworkState.Success(it) }
        }
    }

    fun saveCustomerData(context: Context, data: CustomerResponse.Customer){
        val customer = CustomerData.getInstance(context)
        customer.isLogged = true
        customer.id = data.id
        customer.name = data.first_name
        customer.email = data.email
        customer.currency = data.currency
        customer.favListId = data.note
        customer.cartListId = data.multipass_identifier
    }


    override fun onCleared() {
        super.onCleared()
    }
}

class AuthenticationViewModelFactory (val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)){
            AuthenticationViewModel(repository) as T
        }else{
            throw IllegalArgumentException("AuthenticationViewModel Class Not Found")
        }
    }
}