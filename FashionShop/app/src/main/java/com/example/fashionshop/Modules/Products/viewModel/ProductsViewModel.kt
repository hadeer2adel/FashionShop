package com.example.fashionshop.Modules.Products.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Locale

class ProductsViewModel(private var repository: Repository) : ViewModel() {
    private var _product = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    val product =_product.asStateFlow()

    private var _subProducts: List<Product> = emptyList()
    private val searchSharedFlow = MutableSharedFlow<String>()

    fun getProducts(vendor : String) {
        viewModelScope.launch(Dispatchers.IO){
            repository.getBrandProducts(vendor)
                .catch { e -> _product.value = NetworkState.Failure(e) }
                .collect {
                    response -> _product.value = NetworkState.Success(response)
                    _subProducts = response.products ?: emptyList()
                }
        }
    }

    fun emitSearch(text: String) {
        _product.value = NetworkState.Loading
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
        _product.value = NetworkState.Success(ProductResponse(filteredProducts))
    }

    fun filterProductsByPrice(from: Float?, to: Float?) {
        _product.value = NetworkState.Loading
        viewModelScope.launch {
            val filteredProducts = _subProducts.filter {
                val price = it.variants?.firstOrNull()?.price?.toFloatOrNull() ?: 0f
                val isWithinFrom = from?.let { price >= it } ?: true
                val isWithinTo = to?.let { price <= it } ?: true
                isWithinFrom && isWithinTo
            }
            _product.value = NetworkState.Success(ProductResponse(filteredProducts))
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}