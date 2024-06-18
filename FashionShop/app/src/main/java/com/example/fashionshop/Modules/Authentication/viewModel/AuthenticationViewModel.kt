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
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthenticationViewModel(private var repository: Repository) : ViewModel(){

    private var _customers = MutableStateFlow<NetworkState<CustomerResponse>>(NetworkState.Loading)
    var customers: StateFlow<NetworkState<CustomerResponse>> = _customers

    private var _customer = MutableStateFlow<NetworkState<CustomerResponse>>(NetworkState.Loading)
    var customer: StateFlow<NetworkState<CustomerResponse>> = _customer

    fun createCustomer(customer: CustomerRequest){

        viewModelScope.launch(Dispatchers.IO){
            try {
                val createResponse = repository.createCustomer(customer)
                val id = createResponse.customer!!.id

                val draftOrderResponse = DraftOrderResponse(DraftOrderResponse.DraftOrder())
                val favList = repository.createDraftOrders(draftOrderResponse)
                val cart = repository.createDraftOrders(draftOrderResponse)

                val updateCustomerRequest = UpdateCustomerRequest(
                    UpdateCustomerRequest.Customer(
                        favList.draft_order.id,
                        cart.draft_order.id
                    ))
                val response = repository.updateCustomer(id, updateCustomerRequest)
                Log.i("TAG", response.toString())
                _customer.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _customer.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _customer.value = NetworkState.Failure(e)
            }
        }
    }

    fun updateCustomer(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val draftOrderResponse = DraftOrderResponse(DraftOrderResponse.DraftOrder())
                val favList = repository.createDraftOrders(draftOrderResponse)
                val cart = repository.createDraftOrders(draftOrderResponse)

                val updateCustomerRequest = UpdateCustomerRequest(
                    UpdateCustomerRequest.Customer(
                        favList.draft_order.id,
                        cart.draft_order.id
                    ))
                val response = repository.updateCustomer(id, updateCustomerRequest)
                Log.i("TAG", response.toString())

                _customer.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _customer.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _customer.value = NetworkState.Failure(e)
            }
        }
    }

    fun getCustomerByEmail(email: String){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getCustomerByEmail(email)
                Log.i("TAG", response.toString())
                _customers.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _customers.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _customers.value = NetworkState.Failure(e)
            }
        }
    }

    fun saveCustomerData(context: Context, data: CustomerResponse.Customer){
        val customer = CustomerData.getInstance(context)
        customer.isLogged = true
        customer.id = data.id
        customer.name = data.first_name + " " + data.last_name
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