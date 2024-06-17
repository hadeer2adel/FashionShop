package com.example.fashionshop.Modules.Address.view

interface AddressListener {
    fun deleteAddress(addressId:Long)
    fun setAddressDefault(id:Long,default:Boolean)


}