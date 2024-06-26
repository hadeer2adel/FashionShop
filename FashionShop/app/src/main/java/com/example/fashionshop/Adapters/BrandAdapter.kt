package com.example.fashionshop.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.R
import com.example.fashionshop.databinding.CardBrandBinding

interface BrandClickListener {
    fun onItemClicked(brand: SmartCollection)
}

class BrandAdapter(private val clickListener: BrandClickListener) :
    ListAdapter<SmartCollection, BrandAdapter.BrandViewHolder>(BrandDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        return BrandViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class BrandDiffUtil : DiffUtil.ItemCallback<SmartCollection>() {
        override fun areItemsTheSame(oldItem: SmartCollection, newItem: SmartCollection): Boolean {
            return oldItem.id == newItem.id // Assuming Brand has an id property
        }

        override fun areContentsTheSame(oldItem: SmartCollection, newItem: SmartCollection): Boolean {
            return oldItem == newItem
        }
    }

     class BrandViewHolder(private val binding: CardBrandBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(brand: SmartCollection, clickListener: BrandClickListener) {
            binding.apply {
                tvBrandName.text = brand.title
                Glide
                    .with(binding.root)
                    .load(brand.image?.src)
                    .into(ivBrand)

                cvLayout.setOnClickListener {
                    clickListener.onItemClicked(brand)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): BrandViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CardBrandBinding.inflate(layoutInflater, parent, false)
                return BrandViewHolder(binding)
            }
        }
    }
}




