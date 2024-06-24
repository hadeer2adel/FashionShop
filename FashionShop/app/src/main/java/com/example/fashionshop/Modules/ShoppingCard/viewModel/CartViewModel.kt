package com.example.fashionshop.Modules.ShoppingCard.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.inventoryQuantities
import com.example.fashionshop.Model.originalPrices
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CartViewModel (private val repo: Repository, private var listId: Long
): ViewModel() {

    private var _productCard = MutableStateFlow<NetworkState<DraftOrderResponse>>(NetworkState.Loading)
    var productCard: StateFlow<NetworkState<DraftOrderResponse>> = _productCard
    init {
        getCardProducts()
    }

    fun getCardProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getDraftOrder(listId)
                .catch { _productCard.value = NetworkState.Failure(it) }
                .collect { _productCard.value = NetworkState.Success(it) }
        }
    }

    fun deleteCardProduct(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _productCard.value = NetworkState.Loading
            repo.getDraftOrder(listId)
                .catch { _productCard.value = NetworkState.Failure(it) }
                .collect {
                    val draftOrder = it.draft_order
                    val updatedLineItems = draftOrder.line_items.filter {
                        it.id != id
                    }
                    val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
                    repo.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                        .catch { _productCard.value = NetworkState.Failure(it) }
                        .collect { _productCard.value = NetworkState.Success(it) }
                }
        }
    }

    fun editCardQuantityProduct(id: Long, quantity: Int, price: String, inventoryQuantitiess: String, images: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _productCard.value = NetworkState.Loading
            repo.getDraftOrder(listId)
                .catch { _productCard.value = NetworkState.Failure(it) }
                .collect {
                    val draftOrder = it.draft_order
                    val updatedLineItems = draftOrder.line_items.map { lineItem ->
                        if (lineItem.id == id) {
                            val updatedProperties = lineItem.properties.toMutableList()
                            val existingImageProperty = updatedProperties.find { it.name.contains("ProductImage") }
                            val imageUrl = existingImageProperty?.name?.substringAfter("src=")?.substringBefore(")")

                            lineItem.copy(
                                quantity = quantity,
                                properties = listOf(
                                    DraftOrderResponse.DraftOrder.LineItem.Property(
                                        name = "ProductImage(src=$imageUrl)",
                                        value = "$inventoryQuantitiess*$price"
                                    )
                                )
                            )
                        } else {
                            lineItem
                        }
                    }
                    val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
                    repo.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                        .catch { _productCard.value = NetworkState.Failure(it) }
                        .collect { _productCard.value = NetworkState.Success(it) }
                }
        }
    }

    fun deleteAllCartProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _productCard.value = NetworkState.Loading
            repo.getDraftOrder(listId)
                .catch { _productCard.value = NetworkState.Failure(it) }
                .collect {
                    val draftOrder = it.draft_order
                    val updatedLineItems = if (draftOrder.line_items.isNotEmpty()) {
                        listOf(draftOrder.line_items.first())
                    } else {
                        emptyList()
                    }
                    val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)

                    repo.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                        .catch { _productCard.value = NetworkState.Failure(it) }
                        .collect {
                            inventoryQuantities.clear()
                            originalPrices.clear()
                            _productCard.value = NetworkState.Success(it)
                        }
                }
        }
    }


}
