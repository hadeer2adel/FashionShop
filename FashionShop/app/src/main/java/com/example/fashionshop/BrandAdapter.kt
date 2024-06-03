package com.example.fashionshop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Brand(val name: String, val logoResId: Int)

class BrandAdapter(private val mContext: Context, brandList: List<Brand>) :
    RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {
    private val mBrandList: List<Brand>

    init {
        mBrandList = brandList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.card_brand, parent, false)
        return BrandViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val brand: Brand = mBrandList[position]
        holder.bind(brand)
    }

    override fun getItemCount(): Int {
        return mBrandList.size
    }

    inner class BrandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mNameTextView: TextView
        private val mLogoImageView: ImageView

        init {
            mNameTextView = itemView.findViewById(R.id.tv_brand_name)
            mLogoImageView = itemView.findViewById<ImageView>(R.id.iv_brand)
        }

        fun bind(brand: Brand) {
            mNameTextView.setText(brand.name)
            mLogoImageView.setImageResource(brand.logoResId)
        }
    }
}

