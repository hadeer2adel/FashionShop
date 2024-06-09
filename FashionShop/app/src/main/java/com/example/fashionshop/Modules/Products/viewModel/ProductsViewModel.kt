package com.example.fashionshop.Modules.Products.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProductsViewModel(private var repository: Repository) : ViewModel() {
    private var _product = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    val product =_product.asStateFlow()
    fun getProducts(vendor : String) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response =repository.getBrandProducts(vendor)
                _product.value = NetworkState.Success(response.body()!!)
            } catch (e: HttpException) {
                _product.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _product.value = NetworkState.Failure(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}