package com.example.fashionshop.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.databinding.CardBrandBinding
import com.example.fashionshop.databinding.CardProductBinding

interface ProductClickListener {
    fun onItemClicked(product: Product)
}

class ProductsAdapterNew(private val clickListener: ProductClickListener) :
    ListAdapter<Product, ProductsAdapterNew.ProductViewHolder>(ProductDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ProductDiffUtil : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    class ProductViewHolder(private val binding: CardProductBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product, clickListener: ProductClickListener) {
            binding.apply {
                title.text = product.title
                price.text = product.variants?.get(0)?.price
                Glide
                    .with(binding.root)
                    .load(product.image?.src)
                    .into(image)

                card.setOnClickListener {
                    clickListener.onItemClicked(product)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CardProductBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }
}