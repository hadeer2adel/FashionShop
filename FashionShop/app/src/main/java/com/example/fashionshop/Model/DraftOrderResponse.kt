package com.example.fashionshop.Model

data class DraftOrderResponse(
    val draft_order: DraftOrder? = null
) {

    data class DraftOrder(
        val id: Long? = null,
        val line_items: List<LineItem>? = null
    ) {

        data class LineItem(
            val product_id: Long? = null,
            val title: String? = null,
            val price: String? = null,
            val sku: String? = null,
        )
    }
}