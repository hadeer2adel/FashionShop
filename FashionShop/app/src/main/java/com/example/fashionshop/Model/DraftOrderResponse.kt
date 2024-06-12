package com.example.fashionshop.Model

data class DraftOrderResponse(
    val draft_order: DraftOrder
) {
    data class DraftOrder(
        val id: Long = 0,
        val line_items: List<LineItem> = listOf(LineItem(null, quantity = 1))
    ) {
        data class LineItem(
            val variant_id: Long?,
            val quantity: Int?,
            val id: Long? = null,
            val title: String? = "dummy",
            val price: String? = "1",
            val sku: String? = null,
        )
    }
}