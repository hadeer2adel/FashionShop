package com.example.fashionshop.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.fashionshop.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private val context: Context, private val imageResourceIds: List<Int>, private val isAd: Boolean) : SliderViewAdapter<SliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?): SliderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_ad, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
        viewHolder?.bind(imageResourceIds[position])
    }

    override fun getCount(): Int {
        return imageResourceIds.size
    }

    inner class SliderViewHolder(itemView: View) : ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_discount)

        fun bind(imageResourceId: Int) {
            // Load image from drawable resource
            imageView.setImageResource(imageResourceId)
            if(isAd) {
                imageView.scaleType = ImageView.ScaleType.FIT_XY
            }
        }
    }
}
