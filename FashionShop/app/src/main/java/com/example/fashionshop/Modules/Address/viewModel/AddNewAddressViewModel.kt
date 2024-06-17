package com.example.fashionshop.Modules.Address.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Service.Networking.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddNewAddressViewModel(private val repository: Repository) : ViewModel() {
    private var _addressRequestResult = MutableStateFlow<NetworkState<AddressRequest>>(NetworkState.Loading)
    var addressRequestResult: StateFlow<NetworkState<AddressRequest>> = _addressRequestResult


    fun addSingleCustomerAddress(customer_id: Long,addressRequest: AddressRequest) {
        viewModelScope.launch(Dispatchers.IO) {


                try {
                    val response = repository.AddSingleCustomerAdreess(customer_id,addressRequest)
                    _addressRequestResult.value = NetworkState.Success(response)

                } catch (e: HttpException) {
                    _addressRequestResult.value = NetworkState.Failure(e)
                }catch (e: Exception) {
                    _addressRequestResult.value = NetworkState.Failure(e)
                }
        }
    }
    fun sendAddressRequest(address1:String,address2:String,city:String,company:String,first_name:String,last_name:String,phone:String,province:String,country:String,zip:String
    , name:String ,province_code:String ,country_code:String ,country_name:String,id:Long , customer_id:Long , default:Boolean
    )  {
        val address = Addresse(
           address1,address2,city,company,country,country_code,country_name,customer_id,default,first_name,id,last_name,name,phone,province,province_code,zip
        )
        val addressRequest = AddressRequest(address)
        addSingleCustomerAddress(customer_id,addressRequest)

    }
}