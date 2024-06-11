package com.example.fashionshop.Model

data class LineItemX(
    val admin_graphql_api_id: String,
    val applied_discount: Any,
    val custom: Boolean,
    val fulfillment_service: String,
    val gift_card: Boolean,
    val grams: Int,
    val id: Long,
    val name: String,
    val price: String,
    val product_id: Any,
    val properties: List<Any>,
    val quantity: Int,
    val requires_shipping: Boolean,
    val sku: Any,
    val tax_lines: List<TaxLine>,
    val taxable: Boolean,
    val title: String,
    val variant_id: Any,
    val variant_title: Any,
    val vendor: Any
)