package com.example.fashionshop.Modules.Home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OrderInfoViewModel(private var repository: Repository) : ViewModel() {

    private var _order = MutableStateFlow<NetworkState<OrderResponse>>(NetworkState.Loading)
    val order =_order.asStateFlow()
    fun getOrder(userId: Long) {
        viewModelScope.launch(Dispatchers.IO){
            repository.getSingleOrder(userId)
                .catch {
                        e -> _order.value = NetworkState.Failure(e)
                }
                .collect { response ->
                    _order.value = NetworkState.Success(response)
                }
        }

    }

    override fun onCleared() {
        super.onCleared()
    }



}