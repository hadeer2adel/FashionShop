package com.example.fashionshop.Modules.Orders.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Repository.Repository

class OrdersFactory (val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OrdersViewModel::class.java)){
            OrdersViewModel(repository) as T
        }else{
            throw IllegalArgumentException("OrdersViewModel Class Not Found")
        }
    }
}