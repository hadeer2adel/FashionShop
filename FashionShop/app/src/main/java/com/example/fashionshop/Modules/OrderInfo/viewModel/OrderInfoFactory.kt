package com.example.fashionshop.Modules.Home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Repository.Repository

class OrderInfoFactory (val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OrderInfoViewModel::class.java)){
            OrderInfoViewModel(repository) as T
        }else{
            throw IllegalArgumentException("OrderInfoViewModel Class Not Found")
        }
    }
}