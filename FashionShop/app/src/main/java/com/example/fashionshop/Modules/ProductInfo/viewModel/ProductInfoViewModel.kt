package com.example.fashionshop.Modules.ProductInfo.viewModel
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Adapters.ReviewAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductDetails
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.Review
import com.example.fashionshop.Model.Reviews
import com.example.fashionshop.Model.inventoryQuantities
import com.example.fashionshop.Model.originalPrices
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProductInfoViewModel(private var repository: Repository, private var listId: Long) : ViewModel(){

    private var _product = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    var product: StateFlow<NetworkState<ProductResponse>> = _product

    private var _productCard = MutableStateFlow<NetworkState<DraftOrderResponse>>(NetworkState.Loading)
    var productCard: StateFlow<NetworkState<DraftOrderResponse>> = _productCard

    private var _reviews = MutableStateFlow<NetworkState<List<Review>>>(NetworkState.Loading)
    var reviews: StateFlow<NetworkState<List<Review>>> = _reviews

    private var _productSuggestions = MutableStateFlow<NetworkState<ProductResponse>>(NetworkState.Loading)
    var productSuggestions: StateFlow<NetworkState<ProductResponse>> = _productSuggestions

    fun getProductInfo(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProductById(id)
                .catch { _product.value = NetworkState.Failure(it) }
                .collect { _product.value = NetworkState.Success(it) }
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
    private fun convertProductToLineItem(product: ProductDetails): DraftOrderResponse.DraftOrder.LineItem{
        return DraftOrderResponse.DraftOrder.LineItem(
            null,
            1,
            product.id,
            product.title,
            product.variants?.get(0)?.price,
            product.images.toString(),
//            inventory_quantity = product.variants?.get(0)?.inventory_quantity
        )
    }

    fun insertCardProduct(context: View, product: ProductDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            _productCard.value = NetworkState.Loading
            repository.getDraftOrder(listId)
                .catch { _productCard.value = NetworkState.Failure(it) }
                .collect {
                    val draftOrder = it.draft_order

                    val existingLineItem = draftOrder.line_items.find { it.title == product.title }

                    if (existingLineItem == null) {
                        val updatedLineItems = draftOrder.line_items.toMutableList().apply {
                            add(convertProductToLineItem(product))
                        }
                        val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)

                        repository.updateDraftOrder(listId, DraftOrderResponse(updatedDraftOrder))
                            .catch { _productCard.value = NetworkState.Failure(it) }
                            .collect { _productCard.value = NetworkState.Success(it) }

                        product.variants?.get(0)?.inventory_quantity?.let {
                            inventoryQuantities.add(it)
                        }
                        product.variants?.get(0)?.price?.let {
                            originalPrices.add(it)
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.Main) {
                            Snackbar.make(
                                context,
                                "Item is already in the cart",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.i("ProductInfoViewModel", "insertCardProduct: Already in cart")
                        }
                        _productCard.value =
                            NetworkState.Failure(Exception("Item is already in the cart"))
                    }
                }
        }
    }







    override fun onCleared() {
        super.onCleared()
    }
}

class ProductInfoViewModelFactory (val repository: Repository, val listId: Long): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProductInfoViewModel::class.java)){
            ProductInfoViewModel(repository,listId) as T
        }else{
            throw IllegalArgumentException("ProductInfoViewModel Class Not Found")
        }
    }
}