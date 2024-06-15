package com.example.fashionshop.Model

import java.io.Serializable


//data class LineItemBody(
//    val variant_id: Long?,
//    var quantity: Int?,
//    val id: Long? = null,
//    val title: String? = "dummy",
//    val price: String? = "1",
//    val sku: String? = null,
//): Serializable
//
//data class AddressBody(
//    val first_name: String,
//    val address1: String,
//    val phone: String,
//    val city: String,
//    val zip: String,
//    val province: String?,
//    val country: String,
//    val last_name: String,
//    val address2: String?,
//    val company: String?,
//    val latitude: Double,
//    val longitude: Double,
//    val name: String,
//    val country_code: String,
//    val province_code: String?
//): Serializable
//
//data class CustomerBody(
//    val id: Long,
//    val email: String,
//    val first_name: String,
//    val last_name: String,
//    val currency: String,
//    val default_address: AddressBody
//): Serializable
//
//
//data class OrderBody(
//    //val current_total_price: String,
//    val shipping_address: AddressBody?,
//    val billing_address: AddressBody?,
//    val customer: CustomerBody,
//    val line_items: List<LineItemBody>,
//    val total_tax: Double,
//    val currency: String
//): Serializable
//data class OrderBodyResponse (
//    val order: OrderBody? = null
//)




data class AddressBody(
    val first_name: String,
    val address1: String,
    val phone: String,
    val city: String,
    val zip: String,
    val province: String?,
    val country: String,
    val last_name: String,
    val address2: String?,
    val company: String?,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country_code: String,
    val province_code: String?
): Serializable

data class CustomerBody(
    val id: Long,
    val email: String,
    val first_name: String,
    val last_name: String,
    val state: String, // Assuming 'state' field from JSON
    val email_marketing_consent: MarketingConsent, // Nested object from JSON
    val sms_marketing_consent: MarketingConsent, // Nested object from JSON
    val tags: String,
    val currency: String,
    val default_address: AddressBody
): Serializable

data class MarketingConsent(
    val state: String,
    val opt_in_level: String,
    val consent_updated_at: String? // Nullable field from JSON
): Serializable

data class LineItemBody(
    val variant_id: Long?,
    var quantity: Int?,
    val id: Long? = null,
    val title: String? = "dummy",
    val price: Double? = 1.0,
    val sku: String? = null,

): Serializable

data class OrderBody(
    val billing_address: AddressBody?,
    val shipping_address: AddressBody?,
    val customer: CustomerBody,
    val line_items: List<LineItemBody>,
    val total_tax: Double,
    val currency: String
): Serializable

data class OrderBodyResponse (
    val order: OrderBody? = null
)







