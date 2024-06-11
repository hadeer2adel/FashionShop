package com.example.fashionshop.Modules.Address.view

interface AddressListener {
    fun deleteAddress(addressId:Long)
    fun setAddressDefault(id:Long,default:Boolean)
    fun sendeditChoosenAddressRequest(
    idd: Long, address1: String, address2: String, city: String, company: String,
    country: String, country_code: String, first_name: String, last_name: String,
    latitude: Any, longitude: Any, name: String, phone: String, province: String,
    province_code: Any, zip: String
    )

}