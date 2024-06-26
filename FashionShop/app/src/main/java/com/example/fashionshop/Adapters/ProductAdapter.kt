package com.example.fashionshop.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Modules.Category.view.CategoryListener
import com.example.fashionshop.R
import com.example.fashionshop.View.showDialog
import com.example.fashionshop.databinding.CardProductBinding
import kotlin.random.Random

class ProductAdapter (
    private val context: Context,
    private val onStart: (id: Long, onTrue: ()->Unit, onFalse: ()->Unit) ->Unit,
    private val onClick: (isFav: Boolean, product: Product)->Unit,
    private val onCardClick: (id: Long)->Unit,
        ):ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffUtil()) {

    lateinit var binding: CardProductBinding

    class ProductViewHolder(var binding: CardProductBinding) : RecyclerView.ViewHolder(binding.root)
    private var currencyConversionRate: Double = 1.0 // Default rate


    fun updateCurrencyConversionRate(rate: Double) {
        currencyConversionRate = rate
        notifyDataSetChanged() // Refresh all items with new currency rate
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardProductBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = getItem(position)
        holder.binding.favBtn.visibility = View.GONE
        val onTrue: () -> Unit = {
            holder.binding.favBtn.visibility = View.VISIBLE
            holder.binding.favBtn.setImageResource(R.drawable.ic_favorite_true)
            holder.binding.favBtn.tag = true
        }
        val onFalse: () -> Unit = {
            holder.binding.favBtn.visibility = View.VISIBLE
            holder.binding.favBtn.setImageResource(R.drawable.ic_favorite_false)
            holder.binding.favBtn.tag = false
        }
        data.id?.let { onStart(it, onTrue, onFalse) }

        holder.binding.apply {
            title.text = data.title
          //  price.text = "${data.variants?.get(0)?.price}"
          //  price.text = convertCurrency(data.variants?.get(0)?.price)
            if (CustomerData.getInstance(context).currency == "USD") {
                val priceDouble = data.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
                price.text = convertCurrency(priceDouble)
            } else{ price.text = data.variants?.get(0)?.price
            }
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

            favBtn.setOnClickListener {
                var isFav = holder.binding.favBtn.tag as Boolean
                isFav = !isFav
                if (isFav) {
                    favBtn.setImageResource(R.drawable.ic_favorite_true)
                    holder.binding.favBtn.tag = isFav
                    onClick(isFav, data)
                } else {
                    val onAllow: () -> Unit = {
                        favBtn.setImageResource(R.drawable.ic_favorite_false)
                        holder.binding.favBtn.tag = isFav
                        onClick(isFav, data)                    }
                    showDialog(
                        context,
                        R.string.delete_title,
                        R.string.delete_body,
                        onAllow
                    )
                }
            }
            card.setOnClickListener {
                data.id?.let { it1 -> onCardClick(it1) }
            }
        }
    }
    private fun convertCurrency(amount: Double?): String {
        amount ?: return "" // Handle null or undefined amount gracefully
        val convertedPrice = amount / currencyConversionRate
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