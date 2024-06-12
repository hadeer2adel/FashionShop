package com.example.fashionshop.Modules.Home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(private var repository: Repository) : ViewModel() {
    private var _products: MutableLiveData<PriceRuleCount> = MutableLiveData<PriceRuleCount>()
    val products: LiveData<PriceRuleCount> = _products
    private var _products2: MutableLiveData<PriceRule> = MutableLiveData<PriceRule>()
    val products2: LiveData<PriceRule> = _products2
    private var _brand = MutableStateFlow<NetworkState<BrandResponse>>(NetworkState.Loading)
    val brand =_brand.asStateFlow()

    fun getBrands(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response =repository.getBrands()
                _brand.value = NetworkState.Success(response.body()!!)
            } catch (e: HttpException) {
                _brand.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _brand.value = NetworkState.Failure(e)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
    }


    fun getAdsCount(){
        viewModelScope.launch(Dispatchers.IO) {

            val result = repository.getDiscountCodesCount()
            _products.postValue(result)

        }


        }

    fun getAdsCode(){
        viewModelScope.launch(Dispatchers.IO) {

            val result = repository.getDiscountCodes()
            _products2.postValue(result)

        }


    }

}