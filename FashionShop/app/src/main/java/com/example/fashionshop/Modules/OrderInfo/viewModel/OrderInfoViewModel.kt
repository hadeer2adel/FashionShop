package com.example.fashionshop.Modules.Home.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OrderInfoViewModel(private var repository: Repository) : ViewModel() {

    private var _order = MutableStateFlow<NetworkState<OrderResponse>>(NetworkState.Loading)
    val order =_order.asStateFlow()
    fun getOrder(userId: Long) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getSingleOrder(userId)
                if (response.isSuccessful && response.body() != null) {
                    _order.value = NetworkState.Success(response.body()!!)
                } else {
                    Log.d("getOrder", ":No data found ")
                    _order.value = NetworkState.Failure(Exception("No data found"))
                }
            } catch (e: HttpException) {
                _order.value = NetworkState.Failure(e)
            } catch (e: Exception) {
                _order.value = NetworkState.Failure(e)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
    }



}