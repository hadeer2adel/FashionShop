package com.example.fashionshop.Model

import android.icu.text.CaseMap.Title

data class DraftOrdersRequest(
    val draft_order: DraftOrder
) {
    data class DraftOrder(
        val customer: Customer,
        val line_items: List<LineItem> = listOf(LineItem(null, quantity = 1))
    ) {
        data class Customer(
            val id: Long
        )
        data class LineItem(
            val variant_id: Long?,
            val price: String? = "1",
            val quantity: Int?,
            val title: String? = "dummy",
        )
    }
}