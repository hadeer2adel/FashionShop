package com.example.fashionshop.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.model.Customer
import com.example.fashionshop.model.customers
import com.example.fashionshop.network.Api_Service
import com.example.fashionshop.network.Api_State
import com.example.fashionshop.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressViewModel (private val repo: Repository
): ViewModel() {
    private  var _products:MutableLiveData<customers> = MutableLiveData<customers>()
    val products :LiveData<customers> = _products




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


}

