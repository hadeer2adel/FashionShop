package com.example.fashionshop.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.inventoryQuantities
import com.example.fashionshop.Model.originalPrices
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModel
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModelFactory
import com.example.fashionshop.Modules.ShoppingCard.view.CartFragment
import com.example.fashionshop.Modules.ShoppingCard.view.CartListener
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.databinding.CartItemBinding
import com.google.android.material.snackbar.Snackbar

class CartAdapter(private val listener: CartListener, private val context: Context,private val v :View
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)
    private var items: List<DraftOrderResponse.DraftOrder.LineItem> = emptyList()
    private var currencyConversionRate: Double = 1.0 // Default rate

    fun setCartList(cartItems: List<DraftOrderResponse.DraftOrder.LineItem>) {
        items = cartItems
        notifyDataSetChanged()
    }

    fun updateCurrencyConversionRate(rate: Double) {
        currencyConversionRate = rate
        notifyDataSetChanged() // Refresh all items with new currency rate
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
        // Extract image URL from properties
        val property = item.properties.firstOrNull { it.name.contains("ProductImage") }
        val imageUrl = property?.name?.substringAfter("src=")?.substringBefore(")")

        Log.i("Glide", "onBindViewHolder: $imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.adidas) // Optional: add a placeholder
                .error(R.drawable.logout) // Optional: add an error image
                .into(holder.binding.imageView)
        } else {
            Log.i("Glide", "No image URL found in properties")
        }

        val parts = item.properties.get(0).value.split("*")
        val quantity = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 0
        val price = parts.getOrNull(1)?.trim() ?: "0.00"
        Log.i("quantity", "onBindViewHolder: ${quantity} ")
        Log.i("price", "onBindViewHolder: ${price} ")


        if (CustomerData.getInstance(context).currency == "USD"){
            val priceDouble = price?.toDoubleOrNull() ?: 0.0
            holder.binding.itemPrice.text =  convertCurrency(priceDouble)}
        else{   holder.binding.itemPrice.text = item.price}
        val customer = CustomerData.getInstance(context)
        holder.binding.currency.text = customer.currency
        holder.binding.itemName.text = item.title
        val skuParts = item.sku?.split("-") ?: emptyList()
        val skuToShow = if (skuParts.size >= 3) {
            "${skuParts[1]}-${skuParts[2]}-${skuParts[3]}" // Concatenate parts as needed
        } else {
            item.sku ?: "" // Handle case where sku is null
            }
            holder.binding.itemDetails.text = skuToShow
        holder.binding.quantityText.text = item.quantity.toString()
        holder.binding.deleteIcon.setOnClickListener {
            item.id?.let { nonNullId ->
                Log.i("CartAdapter", "deleteIcon: $nonNullId")
                listener.deleteCart(nonNullId)
            }


            Log.i("list", "onViewCreated: ${inventoryQuantities} , ////  ${originalPrices}")

        }
        holder.binding.decreaseButton.setOnClickListener {
            val currentQuantity = item.quantity ?: 0
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                item.quantity = newQuantity // Update item's quantity
                holder.binding.quantityText.text = newQuantity.toString()


                // Recalculate and update item's price
                holder.binding.itemPrice.text = calculateTotalPrice(item.price, newQuantity)
                Log.i("holder", "onBindViewHolder: ${   holder.binding.itemPrice.text}")
            //    item.price = holder.binding.itemPrice.text.toString() // Update item's price in the model

                // Notify listener and adapter about the updated quantity and price
                listener.sendeditChoosenQuantityRequest(item.id ?: 0, newQuantity, holder.binding.itemPrice.text.toString(),quantity.toString(),holder.binding.imageView.toString())

                // Notify adapter about data change for this item
                notifyItemChanged(position)
            } else {
                Snackbar.make(v, "Cannot decrease below 1 quantity", Snackbar.LENGTH_SHORT).show()

                Log.i("CartAdapter", "Cannot decrease below 1 quantity")
            }
        }
        holder.binding.increaseButton.setOnClickListener {
            val currentQuantity = item.quantity ?: 0
            if (currentQuantity < quantity) {
                val newQuantity = currentQuantity + 1
                item.quantity = newQuantity // Update item's quantity
                holder.binding.quantityText.text = newQuantity.toString()

                // Recalculate and update item's price
                val totalPrice = calculateTotalPrice(price, newQuantity)
                Log.i("totalPrice", "onBindViewHolder: ${totalPrice}")
                holder.binding.itemPrice.text = price
                Log.i("itemPrice", "onBindViewHolder: ${holder.binding.itemPrice.text}")

                item.price = totalPrice // Update item's price in the model

                listener.sendeditChoosenQuantityRequest(item.id ?: 0, newQuantity, totalPrice,quantity.toString(),holder.binding.imageView.toString())

                notifyItemChanged(position)
            } else {
                Snackbar.make(v, "Cannot increase above $quantity quantity", Snackbar.LENGTH_SHORT).show()

                Log.i("CartAdapter", "Cannot increase above $quantity quantity")
                // Optionally, you can show a toast or handle this situation as per your app's UX design
            }
        }
        }
    private fun convertCurrency(amount: Double?): String {
        amount ?: return "" // Handle null or undefined amount gracefully
        val convertedPrice = amount / currencyConversionRate
        return String.format("%.2f", convertedPrice)
    }
    private fun calculateTotalPrice(price: String?, quantity: Int?): String {
        val itemPrice = price?.toDoubleOrNull() ?: 0.0
        val total = itemPrice * (quantity ?: 0)
        return String.format("%.2f", total) // Format total price to 2 decimal places
    }
    private fun parseSkuString(sku: String?): List<String> {
        if (sku == null) return emptyList()
        val regex = """ProductImage\(src=([^,]+)\)""".toRegex()
        return regex.findAll(sku).map { it.groupValues[1].trim() }.toList()
    }
    private fun updateItemTotalPrice(holder: CartViewHolder, item: DraftOrderResponse.DraftOrder.LineItem, quantity: Int) {
        val totalPrice = item.price?.toDoubleOrNull() ?: 0.0
        val updatedPrice = totalPrice * quantity
        holder.binding.itemPrice.text = updatedPrice.toString()
    }

    override fun getItemCount() = items.size
}