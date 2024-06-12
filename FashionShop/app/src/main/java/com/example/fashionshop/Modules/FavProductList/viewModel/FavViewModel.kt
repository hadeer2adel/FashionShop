package com.example.fashionshop.Modules.FavProductList.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FavViewModel(private var repository: Repository, private var listId: Long) : ViewModel(){

    private var _product = MutableStateFlow<NetworkState<DraftOrderResponse>>(NetworkState.Loading)
    var product: StateFlow<NetworkState<DraftOrderResponse>> = _product

    fun getFavProducts(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getDraftOrder(listId)
                _product.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _product.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _product.value = NetworkState.Failure(e)
            }
        }
    }

    fun insertFavProduct(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateDraftOrder(listId)
        }
    }

    fun deleteFavProduct(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateDraftOrder(listId)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}

class FavViewModelFactory (val repository: Repository, val listId: Long): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavViewModel::class.java)){
            FavViewModel(repository, listId) as T
        }else{
            throw IllegalArgumentException("FavViewModel Class Not Found")
        }
    }
}