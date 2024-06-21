package com.example.fashionshop.Modules.OrderDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.OrderBodyResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class OrderDetailsViewModel (private var repository: Repository) : ViewModel() {

    private var _order = MutableStateFlow<NetworkState<OrderBodyResponse>>(NetworkState.Loading)
    val order =_order.asStateFlow()
    private var _productCode = MutableStateFlow<NetworkState<PriceRule>>(NetworkState.Loading)
    var productCode: StateFlow<NetworkState<PriceRule>> = _productCode

    fun getAdsCode(){

        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getDiscountCodes().catch { e-> _productCode.value = NetworkState.Failure(e) }
                    .collect{
                        _productCode.value = NetworkState.Success(it)

                    }
            } catch (e: HttpException) {
                _productCode.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _productCode.value = NetworkState.Failure(e)
            }
        }
    }
    fun createOrder(
        orderBody: Map<String, OrderBody>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =repository.createOrder(orderBody)
                withContext(Dispatchers.Main) {
                    _order.value = NetworkState.Success(response)
                    onSuccess.invoke()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    _order.value = NetworkState.Failure(e)
                    onError.invoke("HTTP Error: ${e.message}")
                }
            }catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _order.value = NetworkState.Failure(e)
                    onError.invoke("Error: ${e.message}")
                }
            }
        }
    }

}
