package com.example.fashionshop.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewAddressViewModel(private val repository: Repository) : ViewModel() {

    private val _addressRequestResult = MutableLiveData<AddressRequest>()
    val addressRequestResult: LiveData<AddressRequest> = _addressRequestResult

    fun addSingleCustomerAddress(address1: String, address2: String, city: String, company: String, firstName: String, lastName: String, phone: String, province: String, country: String, zip: String, name: String,province_code:String,country_code:String,
                                 country_name:String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.AddSingleCustomerAdreess(address1,address2,city,company,firstName,lastName,phone,province,country,zip,name,province_code,country_code,country_name)
                _addressRequestResult.postValue(result)
            } catch (e: Exception) {
                // Handle error
                // Log error message
                // Notify UI about the error
            }
        }
    }


}
