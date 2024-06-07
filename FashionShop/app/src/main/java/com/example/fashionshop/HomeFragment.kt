package com.example.fashionshop
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview
import com.smarteist.autoimageslider.SliderView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sliderView: SliderView = view.findViewById(R.id.imageSlider)

        val imageResourceIds = listOf(R.drawable.coupone,
                                        R.drawable.coupon2,
                                        R.drawable.coupon3)
        val sliderAdapter = SliderAdapter(requireContext(), imageResourceIds, true)
        sliderView.setSliderAdapter(sliderAdapter)

        // Dummy data for brands
        val brands = listOf(
            Brand("Brand 1", R.drawable.adidas),
            Brand("Brand 2", R.drawable.adidas),
            Brand("Brand 3", R.drawable.adidas),
            Brand("Brand 4", R.drawable.adidas),
            Brand("Brand 5", R.drawable.adidas),
            Brand("Brand 6", R.drawable.adidas),
            Brand("Brand 7", R.drawable.adidas)
        )

        val recyclerView: CarouselRecyclerview = view.findViewById(R.id.rv_brands) // Assuming your RecyclerView is a CarouselRecyclerview
        val adapter = BrandAdapter(requireContext(), brands)
        recyclerView.adapter = adapter
    }
}