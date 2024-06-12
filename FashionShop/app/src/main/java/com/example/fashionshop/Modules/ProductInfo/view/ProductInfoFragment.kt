package com.example.fashionshop.Modules.ProductInfo.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionshop.Adapters.SliderAdapter
import com.example.fashionshop.R
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class ProductInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sliderView: SliderView = view.findViewById(R.id.imageSlider)

        val imageResourceIds = listOf(
            R.drawable.img_product2,
            R.drawable.img_product,
            R.drawable.img_product2,
            R.drawable.img_product
        )

//        val sliderAdapter = SliderAdapter(requireContext(),3, imageResourceIds, false)
//        sliderView.setSliderAdapter(sliderAdapter)
//        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
//        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
//        sliderView.setIndicatorSelectedColor(Color.BLACK)
//        sliderView.setIndicatorUnselectedColor(Color.LTGRAY)
//        sliderView.scrollTimeInSec = 2
//        sliderView.startAutoCycle()
    }

}