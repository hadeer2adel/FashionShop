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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.OnlineSliderAdapter
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductDetails
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModelFactory
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModel
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModelFactory
import com.example.fashionshop.Modules.Products.view.ProductsFragmentArgs
import com.example.fashionshop.Modules.Products.view.ProductsFragmentDirections
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
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel
    private lateinit var binding: FragmentProductInfoBinding
    private lateinit var viewModel: ProductInfoViewModel
    private val args: ProductInfoFragmentArgs by navArgs()
    private var isReviewsVisible = false
    private lateinit var adapter: ProductAdapter
    private var currencyConversionRate: Double = 1.0 // Default rate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allCategoryFactory =
            CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCategoryViewModel = ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)
        var d = 0.0

        allCategoryViewModel.getLatestRates()
        lifecycleScope.launch {
            allCategoryViewModel.productCurrency.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> ""
                    is NetworkState.Success -> {
                        d= response.data.rates.EGP
                        Log.i("initViewModel", "initViewModel:${  response.data} ")
                        currencyConversionRate = response.data.rates?.EGP ?: 1.0



                    }
                    is NetworkState.Failure -> ""
                    else -> { }
                }
            }
        }
        val navController = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        setUpRecycleView()
        initViewModel()
        viewModel.getProductInfo(args.productId)

        binding.showReviews.setOnClickListener {
            isReviewsVisible = ! isReviewsVisible
            if (isReviewsVisible) {
                binding.showReviews.text = requireContext().getString(R.string.hide_reviews)
                childFragmentManager.beginTransaction()
                    .add(R.id.reviewFragment, ReviewFragment())
                    .commit()
            } else {
                binding.showReviews.text = requireContext().getString(R.string.show_reviews)
                val childFragment = childFragmentManager.findFragmentById(R.id.reviewFragment)
                if (childFragment != null) {
                    childFragmentManager.beginTransaction()
                        .remove(childFragment)
                        .commit()
                }
            }
        }

        binding.addToCartBtn.setOnClickListener {
            val networkManager: NetworkManager = NetworkManagerImp.getInstance()
            val repository: Repository = RepositoryImp(networkManager)
            val factory = ProductInfoViewModelFactory(repository,CustomerData.getInstance(requireContext()).cartListId)
            viewModel = ViewModelProvider(this, factory).get(ProductInfoViewModel::class.java)
            val product = (viewModel.product.value as? NetworkState.Success)?.data?.product
            if (product != null) {
                viewModel.insertCardProduct(product)
                Toast.makeText(requireContext(), "Product Added Successfully", Toast.LENGTH_SHORT).show()
                viewModel.insertCardProductImage(product)

            } else {
                Toast.makeText(requireContext(), "Product information not available", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setUpRecycleView(){
        val onClick: (product: Product) -> Unit = {
            val networkManager: NetworkManager = NetworkManagerImp.getInstance()
            val repository: Repository = RepositoryImp(networkManager)
            val factory = FavViewModelFactory(repository, CustomerData.getInstance(requireContext()).favListId)
            val favViewModel = ViewModelProvider(this, factory).get(FavViewModel::class.java)

            favViewModel.insertFavProduct(it)
        }
        val onCardClick: (id: Long) -> Unit = {
            val navController = NavHostFragment.findNavController(this)
            val action = ProductInfoFragmentDirections.actionToProductInfoFragment(it)
            navController.navigate(action)
        }
        adapter = ProductAdapter(requireContext(), false, onClick, onCardClick)
        adapter.submitList(emptyList())
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.recycleView.adapter = adapter
    }

    private fun initViewModel() {
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = ProductInfoViewModelFactory(repository,CustomerData.getInstance(requireContext()).cartListId)
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

        lifecycleScope.launch {
            viewModel.productSuggestions.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> onLoading()
                    is NetworkState.Success -> {
                        onSuccess()
                        adapter.submitList(response.data.products?.take(5))
                    }
                    is NetworkState.Failure -> onFailure(response.error.message)
                }
            }
        }
    }
    private fun convertCurrency(amount: Double?): String {
        amount ?: return "" // Handle null or undefined amount gracefully
        val convertedPrice = amount / currencyConversionRate
        return String.format("%.2f", convertedPrice)
    }
    private fun setData(product: ProductDetails) {

        binding.apply {
            Log.i("TAG", "${
                product.id}: ")
            val customer = CustomerData.getInstance(requireContext())
            name.text = product.title
           // price.text = product.variants?.get(0)?.price
            if (customer.currency=="USD"){
                val priceDouble = product.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
                price.text = convertCurrency(priceDouble)

            }else
            {
              price.text = product.variants?.get(0)?.price


            }

            currency.text = customer.currency
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

            product.vendor?.let { viewModel.getProductSuggestions(it) }
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
    }
}