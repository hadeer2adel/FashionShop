package com.example.fashionshop.Modules.Payment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PaymentViewModel (private val repo: Repository
): ViewModel(){

    private var _productPayment = MutableStateFlow<NetworkState<CheckoutSessionResponse>>(NetworkState.Loading)
    var productPayment: StateFlow<NetworkState<CheckoutSessionResponse>> = _productPayment

    fun getPaymentProducts(successUrl: String,cancelUrl: String,customerEmail: String,currency: String,productName: String,productDescription: String,
                        unitAmountDecimal: Int, quantity: Int,mode: String, paymentMethodType: String
    ){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repo.createCheckoutSession(successUrl,cancelUrl,customerEmail,currency,productName,productDescription,unitAmountDecimal,quantity,mode,paymentMethodType)
                _productPayment.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _productPayment.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _productPayment.value = NetworkState.Failure(e)
            }
        }
    }


}