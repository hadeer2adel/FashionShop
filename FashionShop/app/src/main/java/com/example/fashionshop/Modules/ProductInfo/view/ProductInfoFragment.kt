package com.example.fashionshop.Modules.ProductInfo.view

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.OnlineSliderAdapter
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Adapters.VariantAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.ProductDetails
import com.example.fashionshop.Model.Variant
import com.example.fashionshop.Model.inventoryQuantities
import com.example.fashionshop.Model.originalPrices
import com.example.fashionshop.Modules.Authentication.view.LoginActivity
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModelFactory
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModel
import com.example.fashionshop.Modules.ProductInfo.viewModel.ProductInfoViewModelFactory
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.showDialog
import com.example.fashionshop.databinding.FragmentProductInfoBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var favViewModel: FavViewModel
    private val args: ProductInfoFragmentArgs by navArgs()
    private var isReviewsVisible = false
    private lateinit var adapter: ProductAdapter
    private lateinit var variantAdapter: VariantAdapter
    private var isFav = false
    private var variantId = -1L
    private var currencyConversionRate: Double = 1.0

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
        setUpVariantRecycleView()
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






    }






    private fun setUpRecycleView(){
        val onClick: (isFav: Boolean, product: Product) -> Unit = { isFav, product ->
            if (CustomerData.getInstance(requireContext()).isLogged){
                if (isFav) {
                    favViewModel.insertFavProduct(product)
                } else {
                    product.id?.let { it1 -> favViewModel.deleteFavProduct(it1) }
                }
            }
        }
        val onCardClick: (id: Long) -> Unit = {
            val navController = NavHostFragment.findNavController(this)
            val action = ProductInfoFragmentDirections.actionToProductInfoFragment(it)
            navController.navigate(action)
        }
        val onStart: (id: Long, onTrue: ()->Unit, onFalse: ()->Unit) ->Unit = { id, onTrue, onFalse ->
            if (CustomerData.getInstance(requireContext()).isLogged){
                favViewModel.isFavProduct(id, onTrue, onFalse)
            }
        }
        adapter = ProductAdapter(requireContext(), onStart, onClick, onCardClick)

        adapter.submitList(emptyList())
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.recycleView.adapter = adapter
    }

    private fun initViewModel() {
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = ProductInfoViewModelFactory(repository,CustomerData.getInstance(requireContext()).cartListId)
        viewModel = ViewModelProvider(this, factory).get(ProductInfoViewModel::class.java)

        if (CustomerData.getInstance(requireContext()).isLogged){
            val favFactory = FavViewModelFactory(repository, CustomerData.getInstance(requireContext()).favListId)
            favViewModel = ViewModelProvider(this, favFactory).get(FavViewModel::class.java)
        }

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
        viewModel.getProductInfo(args.productId)
        amount ?: return "" // Handle null or undefined amount gracefully
        val convertedPrice = amount / currencyConversionRate
        return String.format("%.2f", convertedPrice)
    }
    private fun setData(product: ProductDetails) {

        binding.apply {
            val customer = CustomerData.getInstance(requireContext())
            name.text = product.title
           // price.text = product.variants?.get(0)?.price
            if (customer.currency=="USD"){
                val priceDouble = product.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
                price.text = convertCurrency(priceDouble)
            }else {
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
            variantAdapter.submitList(product.variants)
            Log.i("handleFavBtn", "setData: ${product} ")
            handleFavBtn(product)
        }
    }

    private fun handleFavBtn(product: ProductDetails) {
        val onTrue: () -> Unit = {
            binding.favBtn.setImageResource(R.drawable.ic_favorite_true)
            isFav = true
        }
        val onFalse: () -> Unit = {
            binding.favBtn.setImageResource(R.drawable.ic_favorite_false)
            isFav = false
        }
        if (CustomerData.getInstance(requireContext()).isLogged) {
            product.id?.let { favViewModel.isFavProduct(it, onTrue, onFalse) }
        }
        binding.favBtn.setOnClickListener {
            if (CustomerData.getInstance(requireContext()).isLogged){
                isFav = !isFav
                if (isFav) {
                    binding.favBtn.setImageResource(R.drawable.ic_favorite_true)
                    val newProduct = Product(
                        product.id,
                        product.title,
                        product.image,
                        product.variants,
                    )
                    favViewModel.insertFavProduct(newProduct)
                } else {
                    val onAllow: () -> Unit = {
                        binding.favBtn.setImageResource(R.drawable.ic_favorite_false)
                        product.id?.let { it1 -> favViewModel.deleteFavProduct(it1) }
                    }
                    showDialog(
                        requireContext(),
                        R.string.delete_title,
                        R.string.delete_body,
                        onAllow
                    )
                }
            }
            else {
                showAlertDialog()
            }
        }
        binding.addToCartBtn.setOnClickListener {
            if (CustomerData.getInstance(requireContext()).isLogged) {
                val networkManager: NetworkManager = NetworkManagerImp.getInstance()
                val repository: Repository = RepositoryImp(networkManager)
                val factory = ProductInfoViewModelFactory(repository,CustomerData.getInstance(requireContext()).cartListId)
                viewModel = ViewModelProvider(this, factory).get(ProductInfoViewModel::class.java)
                //val product = (viewModel.product.value as? NetworkState.Success)?.data?.product
//                val newProduct = ProductDetails(
//                    product?.id,
//                    product?.title,
//                    product.image[],
//                    product.variants,
//                )
                Log.i("product", "onViewCreated: $product")
                    if (variantId==-1L){
                        Snackbar.make(requireView(), requireContext().getString(R.string.must_select_variant), Snackbar.LENGTH_SHORT).show()

                    }else{
                if (product != null) {
                    Log.i("ProductInfoFragment", "onViewCreated: $product")
                    viewModel.insertCardProduct(requireView(), product,variantId)

                    Log.i("list", "onViewCreated: ${inventoryQuantities} , ////  ${originalPrices}")

                } else {
                    Snackbar.make(requireView(), requireContext().getString(R.string.product_not_available), Snackbar.LENGTH_SHORT).show()
                }
            }
            }
            else {
                showAlertDialog()
            }

        }

        lifecycleScope.launch {
            viewModel.productCard.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is NetworkState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(requireView(),requireContext().getString(R.string.add_product_success), Snackbar.LENGTH_SHORT).show()
                        showAlertDialogSucess()
                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(requireView(),response.error.message.toString(), Snackbar.LENGTH_SHORT).show()
                        //    findNavController().navigate(R.id.action_cartFragment)

                    }
                }
            }
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
        Snackbar.make(requireView(),message.toString(), Snackbar.LENGTH_SHORT).show()
    }
    private fun showAlertDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog_layout, null)
        AlertDialog.Builder(requireContext()).apply {
            setView(dialogView)
            setPositiveButton(requireContext().getString(R.string.login)) { dialog, _ ->
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }
            setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun setUpVariantRecycleView(){
        val onCardClick: (id: Long, price: String) -> Unit = { id, price ->
            variantId = id
            binding.price.text = price
        }
        variantAdapter = VariantAdapter(requireContext(), onCardClick)

        variantAdapter.submitList(emptyList())
        binding.variantRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.variantRecyclerView.adapter = variantAdapter
    }
    private fun showAlertDialogSucess() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog_layout_sucessed_card, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 4000)
    }
}