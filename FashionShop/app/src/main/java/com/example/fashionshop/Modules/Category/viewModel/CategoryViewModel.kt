package com.example.fashionshop.Modules.Category.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.ExchangeRatesResponse
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


class CategoryViewModel(private var repository: Repository) : ViewModel() {
    private var _products = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    val products =_products.asStateFlow()
    private var _allProducts: List<Product> = emptyList()
    private var _productCurrency = MutableStateFlow<NetworkState<ExchangeRatesResponse>>(NetworkState.Loading)
    var productCurrency: StateFlow<NetworkState<ExchangeRatesResponse>> = _productCurrency

    fun getProducts() {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response =repository.getProducts()
                val productsList = response.body()?.products ?: emptyList()
                _allProducts = productsList
                _products.value = NetworkState.Success(response.body()!!)
            } catch (e: HttpException) {
                _products.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _products.value = NetworkState.Failure(e)
            }
        }
    }

    fun filterProducts(mainCategory: String, subCategory: String) {
        _products.value = NetworkState.Loading
        viewModelScope.launch {
            var filteredProducts = _allProducts.filter { it.tags?.contains(mainCategory, true) ?: false }
            if (subCategory.isNotBlank()) {
                filteredProducts = filteredProducts.filter { it.product_type.equals(subCategory, true) }
            }
            _products.value = NetworkState.Success(ProductResponse(filteredProducts))
        }
    }

    fun  getLatestRates(){

        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getLatestRates("YisNLpcqTYuAGXP2Nh8QFySPSDOBAuP1")
                _productCurrency.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _productCurrency.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _productCurrency.value = NetworkState.Failure(e)
            }
        }


    }





    override fun onCleared() {
        super.onCleared()
    }
}