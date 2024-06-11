package com.example.fashionshop.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.example.fashionshop.Model.ProductImage
import com.example.fashionshop.R
import com.smarteist.autoimageslider.SliderViewAdapter

class OnlineSliderAdapter(private val context: Context, private val imageResourceIds: List<ProductImage>?) : SliderViewAdapter<OnlineSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?): SliderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_ad, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
        imageResourceIds?.get(position)?.src?.let { viewHolder?.bind(it) }
    }

    override fun getCount(): Int {
        return imageResourceIds?.size ?: 0
    }

    inner class SliderViewHolder(itemView: View) : ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_discount)

        fun bind(imageResourceId: String) {
            // Load image from drawable resource
            Glide.with(context).load(imageResourceId).into(imageView)
        }
    }
}
