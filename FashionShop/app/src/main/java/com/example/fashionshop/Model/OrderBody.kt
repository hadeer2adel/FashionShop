package com.example.fashionshop.Model

import java.io.Serializable


//data class LineItemBody(
//    val variant_id: Long?,
//    var quantity: Int?,
//    val id: Long? = null,
//    val title: String? = "dummy",
//    val price: String? = "1",
//)
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
//)
//
//data class CustomerBody(
//    val id: Long,
//    val email: String,
//    val first_name: String,
//    val last_name: String,
//    val currency: String,
//    val default_address: AddressBody
//)
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

// Address class
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
)

// Customer class
data class EmailMarketingConsentBody(
    val state: String,
    val opt_in_level: String,
    val consent_updated_at: String?
)

data class SmsMarketingConsentBody(
    val state: String,
    val opt_in_level: String,
    val consent_updated_at: String?,
    val consent_collected_from: String
)

data class DefaultAddressBody(
    val id: Long,
    val customer_id: Long,
    val first_name: String,
    val last_name: String,
    val company: String?,
    val address1: String,
    val address2: String?,
    val city: String,
    val province: String?,
    val country: String,
    val zip: String,
    val phone: String,
    val name: String,
    val province_code: String?,
    val country_code: String,
    val country_name: String,
    val default: Boolean
)

data class CustomerBody(
    val id: Long,
    val email: String,
    val created_at: String,
    val updated_at: String,
    val first_name: String,
    val last_name: String,
    val state: String,
    val note: String?,
    val verified_email: Boolean,
    val multipass_identifier: String?,
    val tax_exempt: Boolean,
    val email_marketing_consent: EmailMarketingConsentBody,
    val sms_marketing_consent: SmsMarketingConsentBody,
    val tags: String,
    val currency: String,
    val tax_exemptions: List<String>,
    val admin_graphql_api_id: String,
    val default_address: DefaultAddressBody
)

// LineItem class
data class LineItemBody(
    val variant_id: Long?,
    var quantity: Int?,
    val id: Long? = null,
    val title: String? = "dummy",
    val price: String? = "1",
)

// Order class
data class OrderBody(
    val billing_address: AddressBody,
    val customer: CustomerBody,
    val line_items: List<LineItemBody>,
    val total_tax: Double,
    val currency: String
)


data class OrderBodyResponse (
    val order: OrderBody? = null
)





