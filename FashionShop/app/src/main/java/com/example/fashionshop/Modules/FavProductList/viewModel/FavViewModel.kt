package com.example.fashionshop.Modules.FavProductList.viewModel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.math.log

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

    fun isFavProduct(id: Long, favTrue: ()->Unit, favFalse: ()->Unit){
        viewModelScope.launch(Dispatchers.IO) {
            _product.value = NetworkState.Loading
            val response = repository.getDraftOrder(listId).draft_order.line_items
            if (response.size > 1) {
                val isFav = response.toMutableList().any {
                    val values = it.sku?.split("*")
                    values?.get(0)?.equals(id.toString()) ?: false
                }

                withContext(Dispatchers.Main) {
                    if (isFav) {
                        favTrue()
                    } else {
                        favFalse()
                    }
                }
            }else {
                withContext(Dispatchers.Main) {
                    favFalse()
                }
            }
        }
    }

    private fun convertProductToLineItem(product: Product): DraftOrderResponse.DraftOrder.LineItem{
        return DraftOrderResponse.DraftOrder.LineItem(
            null,
            1,
            product.id,
            product.title,
            product.variants?.get(0)?.price,
            product.id.toString() + "*" + product.image?.src,
        )
    }

    fun insertFavProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            _product.value = NetworkState.Loading
            val draftOrder = repository.getDraftOrder(listId).draft_order
            val updatedLineItems = draftOrder.line_items.toMutableList().apply {
                add(convertProductToLineItem(product))
            }
            val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)

            try {
                val updatedResponse =
                    repository.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                _product.value = NetworkState.Success(updatedResponse)
            } catch (e: Exception) {
                _product.value = NetworkState.Failure(e)
            }
        }
    }


    fun deleteFavProduct(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _product.value = NetworkState.Loading
            val draftOrder = repository.getDraftOrder(listId).draft_order

            val updatedLineItems = draftOrder.line_items.filter {
                val values = it.sku?.split("*")
                val equal = values?.get(0)?.equals(id.toString()) ?: false
                (! equal)
            }

            val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
            try {
                val updatedResponse = repository.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                _product.value = NetworkState.Success(updatedResponse)
            } catch (e: Exception) {
                _product.value = NetworkState.Failure(e)
            }
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