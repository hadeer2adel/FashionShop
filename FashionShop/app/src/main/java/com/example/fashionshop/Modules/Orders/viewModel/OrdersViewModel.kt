package com.example.fashionshop.Modules.Orders.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OrdersViewModel(private var repository: Repository) : ViewModel() {

    private var _orders = MutableStateFlow<NetworkState<OrderResponse>>(NetworkState.Loading)
    val orders =_orders.asStateFlow()

    fun getOrders(userId: Long) {
        viewModelScope.launch(Dispatchers.IO){
            try {

                val response =repository.getCustomerOrders(userId)
                _orders.value = NetworkState.Success(response.body()!!)
            } catch (e: HttpException) {
                _orders.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _orders.value = NetworkState.Failure(e)
            }
        }

    }
    override fun onCleared() {
        super.onCleared()
    }
}