package com.example.fashionshop.Modules.ProductInfo.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.fashionshop.Adapters.OnlineSliderAdapter
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModel
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModelFactory
import com.example.fashionshop.Modules.Products.view.ProductsFragmentArgs
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentProductInfoBinding
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class ProductInfoFragment : Fragment() {

    private lateinit var binding: FragmentProductInfoBinding
    private lateinit var viewModel: ProductInfoViewModel
    private val args: ProductInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        viewModel.getProductInfo(args.productId)
    }

    private fun initViewModel() {
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = ProductInfoViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProductInfoViewModel::class.java)

        lifecycleScope.launch {
            viewModel.product.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> onLoading()
                    is NetworkState.Success -> {
                        onSuccess()
                        response.data.product?.let { setData(it) }
                    }
                    is NetworkState.Failure -> onFailure(response.error.message)
                }
            }
        }
    }

    private fun setData(product: Product) {

        binding.apply {
            name.text = product.title
            price.text = product.variants?.get(0)?.price
            description.text = product.body_html

            val randomRatings = FloatArray(10) { Random.nextFloat() * 5 }
            val randomValue = randomRatings[Random.nextInt(randomRatings.size)]
            ratingBar.rating = randomValue

            val sliderAdapter = OnlineSliderAdapter(requireContext(), product.images)
            sliderView.setSliderAdapter(sliderAdapter)
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            sliderView.setIndicatorSelectedColor(Color.BLACK)
            sliderView.setIndicatorUnselectedColor(Color.LTGRAY)
            sliderView.scrollTimeInSec = 2
            sliderView.startAutoCycle()
        }
    }

    private fun onLoading(){
        binding.progressBar.visibility = View.VISIBLE
        binding.screen.visibility = View.GONE
    }
    private fun onSuccess(){
        binding.progressBar.visibility = View.GONE
        binding.screen.visibility = View.VISIBLE
    }
    private fun onFailure(message: String?){
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        message?.let { Log.i("TAG", it) }
    }
}