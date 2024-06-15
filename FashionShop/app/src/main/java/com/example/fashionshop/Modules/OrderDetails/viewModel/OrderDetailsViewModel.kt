package com.example.fashionshop.Modules.OrderDetails.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OrderDetailsViewModel (private var repository: Repository) : ViewModel() {
    private var _productCode = MutableStateFlow<NetworkState<PriceRule>>(NetworkState.Loading)
    var productCode: StateFlow<NetworkState<PriceRule>> = _productCode
//    private var _products2: MutableLiveData<PriceRule> = MutableLiveData<PriceRule>()
//    val products2: LiveData<PriceRule> = _products2

    fun getAdsCode(){

        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getDiscountCodes()
                _productCode.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _productCode.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _productCode.value = NetworkState.Failure(e)
            }
        }


//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            val result = repository.getDiscountCodes()
//            _productCode.postValue(result)
//
//        }

    }



    }