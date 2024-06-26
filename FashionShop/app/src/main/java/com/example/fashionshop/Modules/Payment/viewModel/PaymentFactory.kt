package com.example.fashionshop.Modules.Payment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.Repository.Repository

class PaymentFactory (private var repo: Repository ):
    ViewModelProvider.Factory

{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PaymentViewModel::class.java)){
            PaymentViewModel(repo) as T
        }else{
            throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}