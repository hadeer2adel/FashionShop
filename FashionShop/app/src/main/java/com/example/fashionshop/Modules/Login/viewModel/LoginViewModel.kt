package com.example.fashionshop.Modules.Login.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.Customer
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private var repository: Repository) : ViewModel(){

    private var _customer = MutableStateFlow<NetworkState<customers>>(NetworkState.Loading)
    var customer: StateFlow<NetworkState<customers>> = _customer


    fun getCustomerByEmail(email: String){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getCustomerByEmail(email)
                _customer.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _customer.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _customer.value = NetworkState.Failure(e)
            }
        }
    }
    fun saveCustomerData(context: Context, data: Customer){
        val customer = CustomerData.getInstance(context)
        customer.id = data.id
        customer.name = data.first_name + " " + data.last_name
        customer.email = data.email
        customer.currency = data.currency
        customer.favListId = (data.note as? String)?.toLongOrNull() ?: 0L
        customer.cartListId = (data.multipass_identifier as? String)?.toLongOrNull() ?: 0L

    }


    override fun onCleared() {
        super.onCleared()
    }
}

class LoginViewModelFactory (val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            LoginViewModel(repository) as T
        }else{
            throw IllegalArgumentException("LoginViewModel Class Not Found")
        }
    }
}