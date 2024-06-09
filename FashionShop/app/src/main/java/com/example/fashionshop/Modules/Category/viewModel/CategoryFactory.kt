package com.example.fashionshop.Modules.Category.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Repository.Repository

class CategoryFactory (val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CategoryViewModel::class.java)){
            CategoryViewModel(repository) as T
        }else{
            throw IllegalArgumentException("CategoryViewModel Class Not Found")
        }
    }
}