package com.example.fashionshop.Model

import java.io.Serializable

data class ProductResponse(
    val products: List<Product>? = null,
    val product: ProductDetails? = null
)

data class Product(
    val id: Long? = null,
    val title: String? = null,
    val image: ProductImage? = null,
    val variants: List<Variant>? = null,
    val tags: String? = null,
    val product_type: String? = null
    ) : Serializable

data class Variant(
    val price: String? = null,
    val inventory_quantity:Int = 1
)

data class ProductImage(
    val src: String? = null
)

data class ProductDetails(
    val id: Long? = null,
    val title: String? = null,
    val body_html: String? = null,
    val image: ProductImage? = null,
    val images: List<ProductImage>? = null,
    val variants: List<Variant>? = null,
    val vendor: String? = null
) : Serializable