package com.example.fashionshop.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewAddressViewModel(private val repository: Repository) : ViewModel() {

    private val _addressRequestResult = MutableLiveData<AddressRequest>()
    val addressRequestResult: LiveData<AddressRequest> = _addressRequestResult



    fun addSingleCustomerAddress(addressRequest: AddressRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.AddSingleCustomerAdreess(addressRequest)
                _addressRequestResult.postValue(result)
            } catch (e: Exception) {
                // Handle error
                // Log error message
                // Notify UI about the error
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
        addSingleCustomerAddress(addressRequest)

    }
}