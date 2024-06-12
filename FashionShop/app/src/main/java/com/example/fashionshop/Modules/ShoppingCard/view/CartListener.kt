package com.example.fashionshop.Modules.ShoppingCard.view

import com.example.fashionshop.Model.TaxLineX

interface CartListener {
    fun deleteCart(id:Long)
    fun sendeditChoosenQuantityRequest(
        id: Long,
        admin_graphql_api_id: String,
        applied_discount: Any?,
        custom: Boolean,
        fulfillment_service: String,
        gift_card: Boolean,
        grams: Int,
        lineItemId: Long,
        name: String,
        price: String,
        product_id: Any?,
        properties: List<Any>,
        quantity: Int,
        requires_shipping: Boolean,
        sku: Any?,
        tax_lines: List<TaxLineX>,
        taxable: Boolean,
        title: String,
        variant_id: Any?,
        variant_title: Any?,
        vendor: Any?
    )


    fun getSubTotal(total:String)

}