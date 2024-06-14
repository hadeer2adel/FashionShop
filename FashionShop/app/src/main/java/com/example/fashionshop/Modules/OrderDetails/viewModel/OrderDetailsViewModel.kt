package com.example.fashionshop.Modules.OrderDetails.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderDetailsViewModel (private var repository: Repository) : ViewModel() {

    private var _products2: MutableLiveData<PriceRule> = MutableLiveData<PriceRule>()
    val products2: LiveData<PriceRule> = _products2

    fun getAdsCode(){
        viewModelScope.launch(Dispatchers.IO) {

            val result = repository.getDiscountCodes()
            _products2.postValue(result)

        }}



    }