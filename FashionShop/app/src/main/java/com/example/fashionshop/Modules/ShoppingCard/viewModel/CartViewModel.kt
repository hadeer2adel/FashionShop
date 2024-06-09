package com.example.fashionshop.Modules.ShoppingCard.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel (private val repo: Repository
): ViewModel() {
    private  var _products: MutableLiveData<DraftOrders> = MutableLiveData<DraftOrders>()
    val products : LiveData<DraftOrders> = _products

    init {
        getAllDraftOrders()
    }



    fun getAllDraftOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "DraftOrders: ViewMOdel")
            val ProductList= repo.getDraftOrders()
            _products.postValue(ProductList)

        }
    }}



