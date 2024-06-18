package com.example.fashionshop.Modules.OrderDetails.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.OrderBodyResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class OrderDetailsViewModel (private var repository: Repository) : ViewModel() {

    private var _products2: MutableLiveData<PriceRule> = MutableLiveData<PriceRule>()
    val products2: LiveData<PriceRule> = _products2
    private var _order = MutableStateFlow<NetworkState<OrderBodyResponse>>(NetworkState.Loading)
    val order =_order.asStateFlow()

    fun getAdsCode(){
        viewModelScope.launch(Dispatchers.IO) {

            val result = repository.getDiscountCodes()
            _products2.postValue(result)

        }
    }

    fun createOrder(
        orderBody: Map<String, OrderBody>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =repository.createOrder(orderBody)
                withContext(Dispatchers.Main) {
                    _order.value = NetworkState.Success(response)
                    onSuccess.invoke() // Call the onSuccess callback when successful
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    _order.value = NetworkState.Failure(e)
                    onError.invoke("HTTP Error: ${e.message}") // Call the onError callback with error message
                }
            }catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _order.value = NetworkState.Failure(e)
                    onError.invoke("Error: ${e.message}") // Call the onError callback with error message
                }
            }
        }
    }



    }