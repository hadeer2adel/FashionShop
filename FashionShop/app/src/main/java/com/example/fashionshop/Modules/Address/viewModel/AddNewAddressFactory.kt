package com.example.fashionshop.Modules.Address.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Repository.Repository

class AddNewAddressFactory  (private var repo: Repository) :
    ViewModelProvider.Factory

{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddNewAddressViewModel::class.java)){
            AddNewAddressViewModel(repo) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}