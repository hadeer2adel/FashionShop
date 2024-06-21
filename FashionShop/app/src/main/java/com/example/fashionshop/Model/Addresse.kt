package com.example.fashionshop.Model

import java.io.Serializable

data class Addresse(
    val address1: String,
    val address2: Any,
    val city: String,
    val company: Any,
    val country: String,
    val country_code: String,
    val country_name: String,
    val customer_id: Long,
    var default: Boolean,
    val first_name: String,
    val id: Long,
    val last_name: String,
    val name: String,
    val phone: String,
    val province: Any,
    val province_code: Any,
    val zip: String
) : Serializable