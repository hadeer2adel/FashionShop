package com.example.fashionshop.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.Review
import com.example.fashionshop.R
import com.example.fashionshop.databinding.CardProductBinding
import com.example.fashionshop.databinding.CardReviewBinding
import kotlin.random.Random

class ReviewAdapter ():ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewDiffUtil()){

    lateinit var binding: CardReviewBinding
    class ReviewViewHolder (var binding: CardReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardReviewBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val data = getItem(position)
        holder.binding.apply {
            name.text = data.name
            review.text = data.description
            ratingBar.rating = data.rate
        }
    }
}

class ReviewDiffUtil : DiffUtil.ItemCallback<Review>(){
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }
}