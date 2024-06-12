package com.example.fashionshop.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Model.DraftOrder
import com.example.fashionshop.Model.PriceRuleX
import com.example.fashionshop.Modules.Home.view.HomeListener
import com.example.fashionshop.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private val context: Context, private val countt:Int, private val imageResourceIds: List<Int>, private val isAd: Boolean, private val  listener: HomeListener) : SliderViewAdapter<SliderAdapter.SliderViewHolder>() {
    private var DiscountCodeList: List<PriceRuleX> = emptyList()
    fun setCartList(cartItems: List<PriceRuleX>) {
        DiscountCodeList = cartItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_ad, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
        viewHolder?.bind(

            imageResourceIds[position]

        )

        val currentCode = DiscountCodeList[position]
        viewHolder?.itemView?.setOnLongClickListener {
            listener.getDiscountCodeLongPreesed(currentCode.title)
            true // Consume the long click event
        }

    }

    override fun getCount(): Int {
        return countt
    }

    inner class SliderViewHolder(itemView: View) : ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_discount)

        fun bind(imageResourceId: Int) {
            // Load image from drawable resource
            imageView.setImageResource(imageResourceId)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
        }
    }
}
