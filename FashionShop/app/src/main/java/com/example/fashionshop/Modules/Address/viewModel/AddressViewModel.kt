package com.example.fashionshop.Modules.Address.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.AddressDefault
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.BillingAddressX
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.DraftOrderX
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.ShippingAddressX
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddressViewModel (private val repo: Repository
): ViewModel() {
    private var _products2: MutableLiveData<DraftOrders> =
        MutableLiveData<DraftOrders>()
    val products2: LiveData<DraftOrders> = _products2

    private var _products = MutableStateFlow<NetworkState<OneCustomer>>(NetworkState.Loading)
    var products: StateFlow<NetworkState<OneCustomer>> = _products

    private var _products1 = MutableStateFlow<NetworkState<AddressUpdateRequest>>(NetworkState.Loading)
    var products1: StateFlow<NetworkState<AddressUpdateRequest>> = _products1

    fun getAllcustomer(id:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "getAllProducts: ViewMOdel")
            try {
                val response = repo.getcustomers(id)
                _products.value = NetworkState.Success(response)
            } catch (e: HttpException) {
                _products.value = NetworkState.Failure(e)
            }catch (e: Exception) {
                _products.value = NetworkState.Failure(e)
            }





        }
    }

    fun editSingleCustomerAddress(customerId:Long,id: Long, addressRequest: AddressDefultRequest) {
        viewModelScope.launch(Dispatchers.IO) {

                try {
                    val response = repo.editSingleCustomerAddress(customerId,id, addressRequest)
                    _products1.value = NetworkState.Success(response)
                    getAllcustomer(customerId)

                } catch (e: HttpException) {
                    _products1.value = NetworkState.Failure(e)
                }catch (e: Exception) {
                    _products1.value = NetworkState.Failure(e)
                }
        }
    }

    fun sendeditAddressRequest(
        id: Long, default: Boolean ,customerId: Long
    ) {
        val address = AddressDefault(
            default
        )
        val addressRequest = AddressDefultRequest(address)
        editSingleCustomerAddress(customerId,id, addressRequest)


    }
//    fun sendeditChoosenAddressRequest(
//        id: Long, address1: String, address2: String, city: String, company: String,
//        country: String, country_code: String, first_name: String, last_name: String,
//        latitude: Any, longitude: Any, name: String, phone: String, province: String,
//        province_code: Any, zip: String
//    ) {
//        val billingAddress = BillingAddressX(
//            address1, address2, city, company, country, country_code, first_name, last_name,
//            latitude, longitude, name, phone, province, province_code, zip
//        )
//        val shippingAddress = ShippingAddressX(
//            address1, address2, city, company, country, country_code, first_name, last_name,
//            latitude, longitude, name, phone, province, province_code, zip
//        )
//        val draftOrder = DraftOrderX(billingAddress, shippingAddress)
//        val addressRequest = editAddressBody(draftOrder)
//        editSingleCustomerAddressDraftOrder(id, addressRequest)
//    }
    fun senddeleteAddressRequest(
        id: Long,
        customerId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("Deleted", "Deleted Address: ${customerId}")
            repo.deleteSingleCustomerAddress(customerId,id)
            getAllcustomer(customerId)
        }
    }

//    fun editSingleCustomerAddressDraftOrder(id: Long, addressRequest: editAddressBody)
//    {
//        viewModelScope.launch(Dispatchers.IO) {
//
//        val result = repo.editSingleCustomerAddressDraftOrder(id, addressRequest)
//        _products2.postValue(result)
//
//    }
//
//    }

}

