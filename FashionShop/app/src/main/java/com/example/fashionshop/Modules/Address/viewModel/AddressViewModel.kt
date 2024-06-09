package com.example.fashionshop.Modules.Address.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.AddressDefault
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressViewModel (private val repo: Repository
): ViewModel() {
    private  var _products:MutableLiveData<OneCustomer> = MutableLiveData<OneCustomer>()
    val products :LiveData<OneCustomer> = _products
    private  var _products1:MutableLiveData<AddressUpdateRequest> = MutableLiveData<AddressUpdateRequest>()
    val products1 :LiveData<AddressUpdateRequest> = _products1



    init {
        getAllcustomer()
    }



    fun getAllcustomer() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "getAllProducts: ViewMOdel")
            val ProductList= repo.getcustomers()
            _products.postValue(ProductList)

        }
    }

    fun editSingleCustomerAddress(id: Long,addressRequest: AddressDefultRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.editSingleCustomerAddress(id,addressRequest)
                _products1.postValue(result)
                getAllcustomer()
            } catch (e: Exception) {
                // Handle error
                // Log error message
                // Notify UI about the error
            }
        }
    }
    fun sendeditAddressRequest(id:Long , default:Boolean
    )  {
        val address = AddressDefault(
            default
        )
        val addressRequest = AddressDefultRequest(address)
        editSingleCustomerAddress(id,addressRequest)

    }


}

