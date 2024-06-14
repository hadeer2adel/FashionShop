package com.example.fashionshop.Modules.Category.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Locale


class CategoryViewModel(private var repository: Repository) : ViewModel() {
    private var _products = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    val products =_products.asStateFlow()
    private var _allProducts: List<Product> = emptyList()
    private var _subProducts: List<Product> = emptyList()
    private val searchSharedFlow = MutableSharedFlow<String>()

    fun getProducts() {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response =repository.getProducts()
                val productsList = response.body()?.products ?: emptyList()
                _allProducts = productsList
                _subProducts = productsList
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

    override fun onCleared() {
        super.onCleared()
    }
}