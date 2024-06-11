package com.example.fashionshop.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Model.DraftOrder
import com.example.fashionshop.Modules.ShoppingCard.view.CartListener
import com.example.fashionshop.databinding.CartItemBinding

class CartAdapter(private val listener: CartListener) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)
    private var items: List<DraftOrder> = emptyList()

    fun setCartList(cartItems: List<DraftOrder>) {
        items = cartItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        Log.i("CartAdapter", "item: ${item.line_items}")

        val lineItems = item.line_items
        for (lineItem in lineItems) {
            holder.binding.itemName.text = lineItem.title ?: "N/A" // null check
            holder.binding.itemPrice.text = lineItem.price?.toString() ?: "0.0" // null check
            holder.binding.quantityText.text = lineItem.quantity.toString()
            holder.binding.decreaseButton.setOnClickListener {
                if (lineItem.quantity > 0) {
                    lineItem.quantity--
                    holder.binding.quantityText.text = lineItem.quantity.toString()
                    listener.sendeditChoosenQuantityRequest(
                        item.id,
                        lineItem.admin_graphql_api_id ?: "", // null check
                        lineItem.applied_discount?.let { // Handle applied_discount as an object
                            mapOf(
                                "title" to "",
                                "description" to "",
                                "value" to "",
                                "value_type" to "",
                                "amount" to ""
                            )
                        } , // null check, pass an empty map if null
                        lineItem.custom,
                        lineItem.fulfillment_service ?: "", // null check
                        lineItem.gift_card,
                        lineItem.grams,
                        lineItem.id,
                        lineItem.name ?: "N/A", // null check
                        lineItem.price?.toString() ?: "0.0", // null check
                        lineItem.product_id,
                        lineItem.properties ?: emptyList(), // null check
                        lineItem.quantity,
                        lineItem.requires_shipping,
                        lineItem.sku,
                        lineItem.tax_lines ?: emptyList(), // null check
                        lineItem.taxable,
                        lineItem.title ?: "N/A", // null check
                        lineItem.variant_id?.toString() ?: "0", // null check
                        lineItem.variant_title ?: "N/A", // null check
                        lineItem.vendor ?: "N/A" // null check
                    )
                }
            }

            holder.binding.increaseButton.setOnClickListener {
                lineItem.quantity++
                holder.binding.quantityText.text = lineItem.quantity.toString()
                listener.sendeditChoosenQuantityRequest(
                    item.id,
                    lineItem.admin_graphql_api_id ?: "", // null check
                    if (lineItem.applied_discount != null) {
                        // Convert applied_discount to a Map if it's not null
                        mapOf("applied_discount" to lineItem.applied_discount)
                    } else {
                        // Pass an empty Map if applied_discount is null
                        emptyMap()
                    },// null check, pass an empty map if null
                    lineItem.custom,
                    lineItem.fulfillment_service ?: "", // null check
                    lineItem.gift_card,
                    lineItem.grams,
                    lineItem.id,
                    lineItem.name ?: "N/A", // null check
                    lineItem.price?.toString() ?: "0.0", // null check
                    lineItem.product_id,
                    lineItem.properties ?: emptyList(), // null check
                    lineItem.quantity,
                    lineItem.requires_shipping,
                    lineItem.sku,
                    lineItem.tax_lines ?: emptyList(), // null check
                    lineItem.taxable,
                    lineItem.title ?: "N/A", // null check
                    lineItem.variant_id?.toString() ?: "0", // null check
                    lineItem.variant_title ?: "N/A", // null check
                    lineItem.vendor ?: "N/A" // null check
                )
            }
        }
        holder.binding.deleteIcon.setOnClickListener {
            Log.i("CartAdapter", "deleteIcon: ${item.id} ")
            listener.deleteCart(item.id)
        }
    }

    override fun getItemCount() = items.size
}
