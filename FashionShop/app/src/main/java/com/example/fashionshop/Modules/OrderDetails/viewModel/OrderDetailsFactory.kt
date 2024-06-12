package com.example.fashionshop.Modules.OrderDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Modules.Home.viewModel.HomeViewModel
import com.example.fashionshop.Repository.Repository

class OrderDetailsFactory(val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OrderDetailsViewModel::class.java)){
            OrderDetailsViewModel(repository) as T
        }else{
            throw IllegalArgumentException("HomeViewModel Class Not Found")
        }
    }
}