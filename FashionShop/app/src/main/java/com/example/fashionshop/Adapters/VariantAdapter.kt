package com.example.fashionshop.Adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.Modules.Category.view.CategoryListener
import com.example.fashionshop.R
import com.example.fashionshop.View.showDialog
import com.example.fashionshop.databinding.CardProductBinding
import com.example.fashionshop.databinding.CardVariantBinding
import com.google.android.material.card.MaterialCardView
import kotlin.random.Random

class VariantAdapter (
    private val context: Context,
    private val onCardClick: (id: Long, price: String)->Unit,
        ):ListAdapter<Variant, VariantAdapter.VariantViewHolder>(VariantDiffUtil()) {

    private lateinit var binding: CardVariantBinding
    private var selectedCard: MaterialCardView? = null

    class VariantViewHolder(var binding: CardVariantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardVariantBinding.inflate(inflater, parent, false)
        return VariantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        val data = getItem(position)

        holder.binding.apply {
            size.text = data.option1
            color.setBackgroundColor(getColorStateList(data.option2.toString()))

            card.setOnClickListener {
                selectedCard?.strokeColor = context.getColor(R.color.light)
                card.strokeColor = context.getColor(R.color.favourite)
                selectedCard = card
                data.id?.let { it1 -> onCardClick(it1, data.price!!) }
            }
        }
    }

    private fun getColorStateList(colorName: String): Int {
        val colorInt = try {
            Color.parseColor(colorName)
        } catch (e: IllegalArgumentException) {
            Color.WHITE
        }
        return colorInt
    }
}

class VariantDiffUtil : DiffUtil.ItemCallback<Variant>(){
    override fun areItemsTheSame(oldItem: Variant, newItem: Variant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Variant, newItem: Variant): Boolean {
        return oldItem == newItem
    }
}