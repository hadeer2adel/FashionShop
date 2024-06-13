package com.example.fashionshop.Modules.ShoppingCard.view

interface CartListener {
    fun deleteCart(id:Long)
     fun sendeditChoosenQuantityRequest(id: Long, quantity: Int,price:String)
}