package com.example.fashionshop.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressViewModel (private val repo: Repository
): ViewModel() {
    private  var _products:MutableLiveData<OneCustomer> = MutableLiveData<OneCustomer>()
    val products :LiveData<OneCustomer> = _products




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

