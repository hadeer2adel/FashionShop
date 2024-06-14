package com.example.fashionshop.Modules.ShoppingCard.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Modules.Address.viewModel.AddressViewModel
import com.example.fashionshop.Repository.Repository

class CartFactory  (private var repo: Repository, val listId: Long) :
    ViewModelProvider.Factory

{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CartViewModel::class.java)){
            CartViewModel(repo,listId) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}