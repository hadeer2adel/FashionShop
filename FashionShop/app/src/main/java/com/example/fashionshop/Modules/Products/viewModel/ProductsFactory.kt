package com.example.fashionshop.Modules.Products.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Repository.Repository

class ProductsFactory (val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProductsViewModel::class.java)){
            ProductsViewModel(repository) as T
        }else{
            throw IllegalArgumentException("HomeViewModel Class Not Found")
        }
    }
}