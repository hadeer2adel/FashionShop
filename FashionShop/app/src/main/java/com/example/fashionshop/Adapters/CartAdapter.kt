package com.example.fashionshop.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Model.CartItem
import com.example.fashionshop.Model.DraftOrder
import com.example.fashionshop.databinding.CartItemBinding

class CartAdapter : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

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
        val lineItems = item.line_items

        for (lineItem in lineItems) {
            holder.binding.itemName.text = lineItem.title
            holder.binding.itemPrice.text = lineItem.price.toString()
            holder.binding.decreaseButton.setOnClickListener {
                if (lineItem.quantity> 0) {
                    lineItem.quantity--
                    holder.binding.quantityText.text = lineItem.quantity.toString()
                }
            }

            holder.binding.increaseButton.setOnClickListener {
                lineItem.quantity++
                holder.binding.quantityText.text = lineItem.quantity.toString()
            }
        }
//        holder.binding.itemName.text = lineItems.title
//        holder.binding.itemPrice.text = item.line_items[0].price.toString()


    }


    override fun getItemCount() = items.size
}
