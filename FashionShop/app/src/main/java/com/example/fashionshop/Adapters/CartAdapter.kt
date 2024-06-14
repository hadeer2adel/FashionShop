package com.example.fashionshop.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.DraftOrder
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Modules.ShoppingCard.view.CartListener
import com.example.fashionshop.databinding.CartItemBinding

class CartAdapter(private val listener: CartListener) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)
    private var items: List<DraftOrderResponse.DraftOrder.LineItem> = emptyList()

    fun setCartList(cartItems: List<DraftOrderResponse.DraftOrder.LineItem>) {
        items = cartItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
//        item.note_attributes.forEach { note ->
//            val imageUrl = note.value
//            Glide.with(holder.itemView.context)
//                .load(imageUrl)
//                .into(holder.binding.imageView)
//        }
      //  Log.i("CartAdapter", "item: ${item.line_items}")
        holder.binding.itemName.text = item.title
        holder.binding.itemPrice.text = item.price
        holder.binding.quantityText.text = item.quantity.toString()
        holder.binding.deleteIcon.setOnClickListener {
            item.id?.let { nonNullId ->
                Log.i("CartAdapter", "deleteIcon: $nonNullId")
                listener.deleteCart(nonNullId)
            }
        }
        holder.binding.decreaseButton.setOnClickListener {
            val currentQuantity = item.quantity
            if (currentQuantity != null && currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                item.quantity = newQuantity // Update item's quantity
                holder.binding.quantityText.text = newQuantity.toString()
                holder.binding.itemPrice.text = calculateTotalPrice(item.price, newQuantity)
                listener.sendeditChoosenQuantityRequest(item.id ?: 0, newQuantity, holder.binding.itemPrice.text.toString() ?: "0.0")
            } else {
                Log.i("CartAdapter", "Cannot decrease below 1 quantity")
            }
        }

        holder.binding.increaseButton.setOnClickListener {
            val currentQuantity = item.quantity ?: 0
            val newQuantity = currentQuantity + 1
            item.quantity = newQuantity // Update item's quantity
            holder.binding.quantityText.text = newQuantity.toString()
            holder.binding.itemPrice.text = calculateTotalPrice(item.price, newQuantity)

            listener.sendeditChoosenQuantityRequest(item.id ?: 0, newQuantity, holder.binding.itemPrice.text.toString() ?: "0.0")
        }
    }

    private fun calculateTotalPrice(price: String?, quantity: Int?): String {
        val itemPrice = price?.toDoubleOrNull() ?: 0.0
        val total = itemPrice * (quantity ?: 0)
        return String.format("%.2f", total) // Format total price to 2 decimal places
    }



    private fun updateItemTotalPrice(holder: CartViewHolder, item: DraftOrderResponse.DraftOrder.LineItem, quantity: Int) {
        val totalPrice = item.price?.toDoubleOrNull() ?: 0.0
        val updatedPrice = totalPrice * quantity
        holder.binding.itemPrice.text = updatedPrice.toString()
    }

    override fun getItemCount() = items.size
}
