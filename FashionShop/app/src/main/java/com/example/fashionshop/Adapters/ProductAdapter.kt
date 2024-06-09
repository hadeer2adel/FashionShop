package com.example.fashionshop.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Model.Product
import com.example.fashionshop.R
import com.example.fashionshop.databinding.CardProductBinding

class ProductAdapter (
    private val context: Context,
    private val isFav: Boolean,
    private val onClick: ()->Unit,
    private val onCardClick: ()->Unit
        ):ListAdapter<String, ProductAdapter.ProductViewHolder>(ProductDiffUtil()){

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
            title.text = data
            if(isFav){
                favBtn.setImageResource(R.drawable.ic_favorite_true)
            }
            else{
                favBtn.setImageResource(R.drawable.ic_favorite_false)
            }
            favBtn.setOnClickListener { onClick() }
            card.setOnClickListener { onCardClick() }
        }
    }
}

class ProductDiffUtil : DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.last() == newItem.last()
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}