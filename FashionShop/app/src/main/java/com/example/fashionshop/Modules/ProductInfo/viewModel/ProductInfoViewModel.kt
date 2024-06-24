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
            repository.getBrandProducts(vendor)
                .catch { _productSuggestions.value = NetworkState.Failure(it) }
                .collect { _productSuggestions.value = NetworkState.Success(it) }
        }
    }
    private fun convertProductToLineItem(product: ProductDetails,v_id:Long): DraftOrderResponse.DraftOrder.LineItem{
        return DraftOrderResponse.DraftOrder.LineItem(
            v_id,
            1,
            product.id,
            product.title,
            product.variants?.get(0)?.price,
            product.images.toString(),
            product_id = 38373737,
            properties = listOf(
                DraftOrderResponse.DraftOrder.LineItem.Property(product.images?.get(0).toString(), "Happy Birthday Mom!")
            )
        )
    }



    fun insertCardProduct(context: View, product: ProductDetails, v_id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _productCard.value = NetworkState.Loading
            repository.getDraftOrder(listId)
                .catch { _productCard.value = NetworkState.Failure(it) }
                .collect { draftOrderResponse ->
                    val draftOrder = draftOrderResponse.draft_order

                    // Check if variant_id already exists in line_items
                    val existingLineItem = draftOrder.line_items.find { it.variant_id == v_id }
                    if (existingLineItem == null) {
                        val updatedLineItems = draftOrder.line_items.toMutableList().apply {
                            add(convertProductToLineItem(product, v_id))
                        }
                        val updatedDraftOrder = draftOrder.copy(line_items = updatedLineItems)
                        Log.i("updatedDraftOrder", "insertCardProduct: $updatedDraftOrder")
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
                        _productCard.value = NetworkState.Failure(Exception("Item is already in the cart"))
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