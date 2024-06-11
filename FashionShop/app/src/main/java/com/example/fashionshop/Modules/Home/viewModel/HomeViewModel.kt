package com.example.fashionshop.Modules.Home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(private var repository: Repository) : ViewModel() {

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
}