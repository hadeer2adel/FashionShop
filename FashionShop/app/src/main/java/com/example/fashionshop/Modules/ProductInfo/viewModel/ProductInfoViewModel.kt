package com.example.fashionshop.Modules.ProductInfo.viewModel
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Adapters.ReviewAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.Review
import com.example.fashionshop.Model.Reviews
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProductInfoViewModel(private var repository: Repository) : ViewModel(){

    private var _product = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    var product: StateFlow<NetworkState<ProductResponse>> = _product

    private var _reviews = MutableStateFlow<NetworkState<List<Review>>>(NetworkState.Loading)
    var reviews: StateFlow<NetworkState<List<Review>>> = _reviews

    private var _productSuggestions = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    var productSuggestions: StateFlow<NetworkState<ProductResponse>> = _productSuggestions

    fun getProductInfo(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getProductById(id)
                _product.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _product.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _product.value = NetworkState.Failure(e)
            }
        }
    }

    fun getReviews(){
        val reviewList = Reviews()
        _reviews.value = NetworkState.Success(reviewList.getReviews())
    }

    fun getProductSuggestions(vendor : String){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response =repository.getBrandProducts(vendor)
                _productSuggestions.value = NetworkState.Success(response.body()!!)
            } catch (e: HttpException) {
                _productSuggestions.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _productSuggestions.value = NetworkState.Failure(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}

class ProductInfoViewModelFactory (val repository: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProductInfoViewModel::class.java)){
            ProductInfoViewModel(repository) as T
        }else{
            throw IllegalArgumentException("ProductInfoViewModel Class Not Found")
        }
    }
}