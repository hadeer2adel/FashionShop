package com.example.fashionshop.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Model.CartItem
import com.example.fashionshop.databinding.CartItemBinding

class CartAdapter(private val items: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemName.text = item.name
        holder.binding.itemPrice.text = item.price.toString()

        holder.binding.decreaseButton.setOnClickListener {
            if (item.quantity > 0) {
                item.quantity--
                holder.binding.quantityText.text = item.quantity.toString()
            }
        }

        holder.binding.increaseButton.setOnClickListener {
            item.quantity++
            holder.binding.quantityText.text = item.quantity.toString()
        }
    }


    override fun getItemCount() = items.size
}
