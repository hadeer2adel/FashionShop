package com.example.fashionshop.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.Product
import com.example.fashionshop.R
import com.example.fashionshop.databinding.CardProductBinding
import kotlin.random.Random

class ProductAdapter (
    private val context: Context,
    private val isFav: Boolean,
    private val onClick: ()->Unit,
    private val onCardClick: (id: Long)->Unit
        ):ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffUtil()){

    lateinit var binding: CardProductBinding
    class ProductViewHolder (var binding: CardProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardProductBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = getItem(position)
        holder.binding.apply {
            title.text = data.title
            var currency = "USD"
            price.text = "${data.variants?.get(0)?.price} $currency"
            Glide
                .with(binding.root)
                .load(data.image?.src)
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
            favBtn.setOnClickListener { onClick() }
            card.setOnClickListener {
                data.id?.let { it1 -> onCardClick(it1) }
            }
        }
    }
}

class ProductDiffUtil : DiffUtil.ItemCallback<Product>(){
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}