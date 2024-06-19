package com.example.fashionshop.Modules.ShoppingCard.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderEditingQuntity
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.Images
import com.example.fashionshop.Model.LineItem
import com.example.fashionshop.Model.TaxLineX
import com.example.fashionshop.Model.editOrderQuantityBody
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CartViewModel (private val repo: Repository, private var listId: Long
): ViewModel() {

    private var _productCard = MutableStateFlow<NetworkState<DraftOrderResponse>>(NetworkState.Loading)
    var productCard: StateFlow<NetworkState<DraftOrderResponse>> = _productCard
    private var _productCardImage = MutableStateFlow<NetworkState<Images>>(NetworkState.Loading)
    var productCardImage : StateFlow<NetworkState<Images>> = _productCardImage
    init {
        getCardProducts()
    }

    fun getCardProducts(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repo.getDraftOrder(listId)
                _productCard.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _productCard.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _productCard.value = NetworkState.Failure(e)
            }
        }
    }
    fun getCardProductsImages(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repo.getProductImage(id)
                _productCardImage.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _productCardImage.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _productCardImage.value = NetworkState.Failure(e)
            }
        }
    }

    fun deleteCardProduct(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _productCard.value = NetworkState.Loading
            val draftOrder = repo.getDraftOrder(listId).draft_order
            val updatedLineItems = draftOrder.line_items.filter {
                it.id != id
            }
            val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
            try {
                val updatedResponse = repo.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                _productCard.value = NetworkState.Success(updatedResponse)
            } catch (e: Exception) {
                _productCard.value = NetworkState.Failure(e)
            }
        }
    }

    fun editCardQuantityProduct(id: Long, quantity: Int, price: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _productCard.value = NetworkState.Loading
            val draftOrderResponse = repo.getDraftOrder(listId)
            val draftOrder = draftOrderResponse.draft_order
            val updatedLineItems = draftOrder.line_items.map { lineItem ->
                if (lineItem.id == id) {
                    lineItem.copy(quantity = quantity, price = price)
                } else {
                    lineItem
                }
            }
            val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)

            try {
                val updatedResponse = repo.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                _productCard.value = NetworkState.Success(updatedResponse)
            } catch (e: Exception) {
                _productCard.value = NetworkState.Failure(e)
            }
        }
    }
    fun deleteAllCartProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _productCard.value = NetworkState.Loading
            val draftOrder = repo.getDraftOrder(listId).draft_order
            val updatedLineItems = if (draftOrder.line_items.isNotEmpty()) {
                listOf(draftOrder.line_items.first())
            } else {
                emptyList()
            }
            val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
            Log.i("updatedDraftOrder", "deleteAllCartProducts: ${updatedDraftOrder} ")
            try {
                val updatedResponse = repo.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                _productCard.value = NetworkState.Success(updatedResponse)
            } catch (e: Exception) {
                _productCard.value = NetworkState.Failure(e)
            }
        }
    }

//    fun deteteDrafOrder(id: Long)
//    {
//
//        viewModelScope.launch(Dispatchers.IO){
//            try {
//            repo.deleteSingleCustomerDrafOrder(id)
//                getCardProducts()
//            } catch (e: HttpException) {
//                _productCardImage.value = NetworkState.Failure(e)
//            }catch (e: Exception) {
//                _productCardImage.value = NetworkState.Failure(e)
//            }
//        }
//
//    }

}
