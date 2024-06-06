package com.example.fashionshop.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.repository.Repository

class AddressFactory (private var repo: Repository) :
    ViewModelProvider.Factory

{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddressViewModel::class.java)){
            AddressViewModel(repo)as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}