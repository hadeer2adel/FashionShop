package com.example.fashionshop.Modules.Category.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.ExchangeRatesResponse
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Locale


class CategoryViewModel(private var repository: Repository) : ViewModel() {
    private var _products = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    val products =_products.asStateFlow()
    private var _allProducts: List<Product> = emptyList()
    private var _subProducts: List<Product> = emptyList()
    private val searchSharedFlow = MutableSharedFlow<String>()
    private var _productCurrency = MutableStateFlow<NetworkState<ExchangeRatesResponseX>>(NetworkState.Loading)
    var productCurrency: StateFlow<NetworkState<ExchangeRatesResponseX>> = _productCurrency

    fun getProducts() {
        viewModelScope.launch(Dispatchers.IO){
            repository.getProducts()
                .catch { e -> _products.value = NetworkState.Failure(e) }
                .collect { response ->
                    val productsList = response.products ?: emptyList()
                    _allProducts = productsList
                    _subProducts = productsList
                    _products.value = NetworkState.Success(response)
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
            _subProducts = filteredProducts
            _products.value = NetworkState.Success(ProductResponse(filteredProducts))
        }
    }

    fun emitSearch(text: String) {
        _products.value = NetworkState.Loading
        viewModelScope.launch {
            searchSharedFlow.emit(text)
        }
    }

    fun collectSearch() {
        viewModelScope.launch {
            searchSharedFlow.collect { reactiveSearch(it) }
        }
    }

    private fun reactiveSearch(s: String) {
        val filteredProducts = _subProducts.filter {
            it.title?.lowercase(Locale.getDefault())?.startsWith(s) ?:  false ||
                    it.title?.lowercase(Locale.getDefault())?.contains(s) ?: false
        }.toList()
        _products.value = NetworkState.Success(ProductResponse(filteredProducts))
    }
    fun  getLatestRates(){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = "B7rLh6S27tkxdyJVatJh2lrRdo8sd7aL"
                val symbols = "EGP" // Replace with desired symbols
                val base = "USD"
                val response = repository.getExchangeRates(apiKey,symbols, base)
                Log.d("ViewModel", "Exchange rates response: $response")
                _productCurrency.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                Log.e("ViewModel", "HttpException: ${e.message()}")
                _productCurrency.value = NetworkState.Failure(e)
            } catch (e: Exception) {
                Log.e("ViewModel", "Exception: ${e.message}")
                _productCurrency.value = NetworkState.Failure(e)
            }
        }


    }

    fun filterProductsByPrice(from: Float?, to: Float?) {
        _products.value = NetworkState.Loading
        viewModelScope.launch {
            val filteredProducts = _subProducts.filter {
                val price = it.variants?.firstOrNull()?.price?.toFloatOrNull() ?: 0f
                val isWithinFrom = from?.let { price >= it } ?: true
                val isWithinTo = to?.let { price <= it } ?: true
                isWithinFrom && isWithinTo
            }
            _products.value = NetworkState.Success(ProductResponse(filteredProducts))
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}