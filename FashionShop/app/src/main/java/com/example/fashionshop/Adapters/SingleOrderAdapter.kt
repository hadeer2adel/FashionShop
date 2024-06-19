package com.example.fashionshop.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.LineItemBody
import com.example.fashionshop.R
import com.example.fashionshop.databinding.CardOrderDetailBinding

class SingleOrderAdapter(private val context: Context)
    : ListAdapter<LineItemBody, SingleOrderAdapter.OrderViewHolder>(SingleOrderDiffUtil()){

    lateinit var binding: CardOrderDetailBinding
    class OrderViewHolder (var binding: CardOrderDetailBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardOrderDetailBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.binding.apply {
            tvTitle.text = order.title
            tvQuantity.text = order.quantity.toString()
            tvPrice.text = order.price
//            val values = order.sku?.split("*")
//            val imageUrl = values?.get(1)
//            Glide
//                .with(binding.root)
//                .load(imageUrl)
//                .into(ivProduct)
            val imageUrls = parseSkuString(order.sku)
            Log.i("Glide", "Parsed Image URLs: $imageUrls")

            if (imageUrls.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(imageUrls[0])
                    .placeholder(R.drawable.adidas) // Optional: add a placeholder
                    .error(R.drawable.logout) // Optional: add an error image
                    .into(ivProduct)
            } else {
                Log.i("Glide", "No image URLs found in SKU: ${order.sku}")
            }
        }
    }

    private fun parseSkuString(sku: String?): List<String> {
        if (sku == null) return emptyList()
        val regex = """ProductImage\(src=([^,]+)\)""".toRegex()
        return regex.findAll(sku).map { it.groupValues[1].trim() }.toList()
    }
}


class SingleOrderDiffUtil : DiffUtil.ItemCallback<LineItemBody>() {
    override fun areItemsTheSame(oldItem: LineItemBody, newItem: LineItemBody): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LineItemBody, newItem: LineItemBody): Boolean {
        return oldItem == newItem
    }
}