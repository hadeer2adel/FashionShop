package com.example.fashionshop.Model

data class CustomerRequest(val customer: Customer) {
    data class Customer(
        val first_name: String?,
        val last_name: String?,
        val email: String?
        )
}

data class CustomerResponse(val customer: Customer) {
    data class Customer(
        val id: Long,
        val first_name: String,
        val last_name: String,
        val email: String,
        val phone: String,
        val currency: String,
    )
}