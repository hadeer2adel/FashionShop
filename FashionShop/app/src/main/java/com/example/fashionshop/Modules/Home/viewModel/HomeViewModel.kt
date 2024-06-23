package com.example.fashionshop.Modules.Home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(private var repository: Repository) : ViewModel() {
    private var _brand = MutableStateFlow<NetworkState<BrandResponse>>(NetworkState.Loading)
    val brand: StateFlow<NetworkState<BrandResponse>> = _brand.asStateFlow()
    private var _products = MutableStateFlow<NetworkState<PriceRule>>(NetworkState.Loading)
    var products: StateFlow<NetworkState<PriceRule>> = _products

    fun getBrands(){
        viewModelScope.launch(Dispatchers.IO){
            repository.getBrands()
            .catch {
                e -> _brand.value = NetworkState.Failure(e)
            }
            .collect { response ->
                _brand.value = NetworkState.Success(response)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
    }


    fun getAdsCode(){

        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getDiscountCodes().catch { e->_products.value = NetworkState.Failure(e) }
                    .collect{it->
                        _products.value = NetworkState.Success(it)
                    }
            } catch (e: HttpException) {
                _products.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _products.value = NetworkState.Failure(e)
            }
        }
    }

}