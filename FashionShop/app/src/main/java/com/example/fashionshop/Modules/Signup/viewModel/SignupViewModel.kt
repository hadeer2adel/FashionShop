package com.example.fashionshop.Modules.Signup.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private var repository: Repository) : ViewModel(){

    private var _customer = MutableStateFlow<NetworkState<CustomerResponse>>(NetworkState.Loading)
    var customer: StateFlow<NetworkState<CustomerResponse>> = _customer


    fun createCustomer(customer: CustomerRequest){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.createCustomer(customer)
                _customer.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _customer.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _customer.value = NetworkState.Failure(e)
            }
        }
    }

    fun saveCustomerData(context: Context, data: CustomerResponse.Customer){
        val customer = CustomerData.getInstance(context)
        customer.id = data.id
        customer.name = data.first_name + " " + data.last_name
        customer.email = data.email
        customer.currency = data.currency
    }

    override fun onCleared() {
        super.onCleared()
    }
}

class SignupViewModelFactory (val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SignupViewModel::class.java)){
            SignupViewModel(repository) as T
        }else{
            throw IllegalArgumentException("SignupViewModel Class Not Found")
        }
    }
}