package com.example.fashionshop.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.R
import com.example.fashionshop.databinding.CardProductBinding
import kotlin.random.Random

class FavProductAdapter (
    private val context: Context,
    private val isFav: Boolean,
    private val onClick: (id: Long)->Unit,
    private val onCardClick: (id: Long)->Unit
        ):ListAdapter<DraftOrderResponse.DraftOrder.LineItem, ProductAdapter.ProductViewHolder>(FavProductDiffUtil()){

    lateinit var binding: CardProductBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ProductViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardProductBinding.inflate(inflater, parent, false)
        return ProductAdapter.ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        val data = getItem(position)
        holder.binding.apply {
            title.text = data.title
            price.text = "${data.price}"
            val customer = CustomerData.getInstance(context)
            currency.text = customer.currency
            Glide
                .with(binding.root)
                .load(data.sku)
                .into(image)
            val randomRatings = FloatArray(10) { Random.nextFloat() * 5 }
            val randomValue = randomRatings[Random.nextInt(randomRatings.size)]
            ratingBar.rating = randomValue

            if(isFav){
                favBtn.setImageResource(R.drawable.ic_favorite_true)
            }
            else{
                favBtn.setImageResource(R.drawable.ic_favorite_false)
            }
            favBtn.setOnClickListener {
                data.id?.let { it1 -> onClick(it1) }
            }
            card.setOnClickListener {
                data.id?.let { it1 -> onCardClick(it1) }
            }
        }
    }
}

class FavProductDiffUtil : DiffUtil.ItemCallback<DraftOrderResponse.DraftOrder.LineItem>(){
    override fun areItemsTheSame(oldItem: DraftOrderResponse.DraftOrder.LineItem, newItem: DraftOrderResponse.DraftOrder.LineItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DraftOrderResponse.DraftOrder.LineItem, newItem: DraftOrderResponse.DraftOrder.LineItem): Boolean {
        return oldItem == newItem
    }
}