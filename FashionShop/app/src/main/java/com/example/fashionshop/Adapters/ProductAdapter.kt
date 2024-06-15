package com.example.fashionshop.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Modules.Category.view.CategoryListener
import com.example.fashionshop.R
import com.example.fashionshop.databinding.CardProductBinding
import kotlin.random.Random

class ProductAdapter (
    private val context: Context,
    private var isFav: Boolean,
    private val onClick: (product: Product)->Unit,
    private val onCardClick: (id: Long)->Unit,
        ):ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffUtil()){

    lateinit var binding: CardProductBinding
    class ProductViewHolder (var binding: CardProductBinding) : RecyclerView.ViewHolder(binding.root)
    private var currencyConversionRate: Double = 1.0 // Default rate

    fun updateCurrencyConversionRate(rate: Double) {
        currencyConversionRate = rate
        notifyDataSetChanged() // Refresh all items with new currency rate
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardProductBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = getItem(position)
        holder.binding.apply {
            title.text = data.title
          //  price.text = "${data.variants?.get(0)?.price}"
          //  price.text = convertCurrency(data.variants?.get(0)?.price)
            val priceDouble = data.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
            price.text = convertCurrency(priceDouble)

            val customer = CustomerData.getInstance(context)
            Log.i("customer", "${customer.currency}")
//            if (customer.currency == "EGY") {

            currency.text = customer.currency
//        }

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
            favBtn.setOnClickListener {
                isFav = ! isFav
                onClick(data)
                if(isFav){
                    favBtn.setImageResource(R.drawable.ic_favorite_true)
                }
                else{
                    favBtn.setImageResource(R.drawable.ic_favorite_false)
                }
            }
            card.setOnClickListener {
                data.id?.let { it1 -> onCardClick(it1) }
            }
        }
    }
    private fun convertCurrency(amount: Double?): String {
        amount ?: return "" // Handle null or undefined amount gracefully
        val convertedPrice = amount * currencyConversionRate
        return String.format("%.2f", convertedPrice)
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