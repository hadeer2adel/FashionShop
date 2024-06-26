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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.math.log

class FavViewModel(private var repository: Repository, private var listId: Long) : ViewModel(){

    private var _product = MutableStateFlow<NetworkState<DraftOrderResponse>>(NetworkState.Loading)
    var product: StateFlow<NetworkState<DraftOrderResponse>> = _product

    fun getFavProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDraftOrder(listId)
                .catch { _product.value = NetworkState.Failure(it) }
                .collect { _product.value = NetworkState.Success(it) }
        }
    }

    fun isFavProduct(id: Long, favTrue: ()->Unit, favFalse: ()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _product.value = NetworkState.Loading
            repository.getDraftOrder(listId)
                .catch { _product.value = NetworkState.Failure(it) }
                .collect {
                    val response = it.draft_order.line_items
                    if (response.size > 1) {
                        val isFav = response.toMutableList().any {
                            val values = it.sku?.split("*")
                            values?.get(0)?.equals(id.toString()) ?: false
                        }

                        withContext(Dispatchers.Main) {
                            if (isFav) favTrue() else favFalse()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            favFalse()
                        }
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
            repository.getDraftOrder(listId)
                .catch { _product.value = NetworkState.Failure(it) }
                .collect {
                    val draftOrder = it.draft_order

                    val updatedLineItems = draftOrder.line_items.toMutableList().apply {
                        add(convertProductToLineItem(product))
                    }

                    val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
                    repository.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                        .catch { _product.value = NetworkState.Failure(it) }
                        .collect { _product.value = NetworkState.Success(it) }
                }
        }
    }


    fun deleteFavProduct(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _product.value = NetworkState.Loading
            repository.getDraftOrder(listId)
                .catch { _product.value = NetworkState.Failure(it) }
                .collect {
                    val draftOrder = it.draft_order

                    val updatedLineItems = draftOrder.line_items.filter {
                        val values = it.sku?.split("*")
                        val equal = values?.get(0)?.equals(id.toString()) ?: false
                        (!equal)
                    }

                    val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
                    repository.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                        .catch { _product.value = NetworkState.Failure(it) }
                        .collect { _product.value = NetworkState.Success(it) }
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