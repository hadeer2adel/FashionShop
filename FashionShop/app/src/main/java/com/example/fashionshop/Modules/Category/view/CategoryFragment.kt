package com.example.fashionshop.Modules.Category.view

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.FavProductList.view.FavoriteFragmentDirections
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModelFactory
import com.example.fashionshop.Modules.Products.view.ProductsFragmentDirections
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.isNetworkConnected
import com.example.fashionshop.databinding.FragmentCategoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton

class CategoryFragment : Fragment() ,CategoryListener{
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    var isAllFabsVisible: Boolean? = null
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: CategoryViewModel
    private var mainCategory = ""
    private var subCategory = ""
    private lateinit var favViewModel: FavViewModel
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabClock: Animation
    private lateinit var fabAntiClock: Animation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        if (isNetworkConnected(requireContext())){

            isAllFabsVisible = false


            binding.fabCategory.setOnClickListener {
                isAllFabsVisible = if (isAllFabsVisible == false) {
                    showAllFabButtons()
                    binding.fabCategory.setImageResource(R.drawable.ic_close)
                    binding.fabCategory.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    decreaseBrightness()
                    true
                } else {
                    increaseBrightness()
                    binding.fabCategory.setImageResource(R.drawable.ic_categories)
                    hideAllFabButtons()
                    binding.fabCategory.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                    false
                }

            }

            binding.filterBtn.setOnClickListener {
                showFilterMenu(requireContext())
            }
            setUpRV()
            allCategoryFactory =
                CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
            allCategoryViewModel = ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)
            var d = 0.0

            allCategoryViewModel.getLatestRates()
            lifecycleScope.launch {
                allCategoryViewModel.productCurrency.collectLatest { response ->
                    when(response){
                        is NetworkState.Loading -> showLoading()
                        is NetworkState.Success -> {
                            d= response.data.rates.EGP
                            Log.i("initViewModel", "initViewModel:${  response.data} ")
                            val exchangeRate = response.data.rates?.EGP ?: 1.0 // Default to 1.0 if rate is not available
                            updateCurrencyRates(exchangeRate)


                        }
                        is NetworkState.Failure -> Log.i("TAG", "onViewCreated: \"Failed ttgtgtgtgto load data. Please try again.\"")
                            //showError("Network Error", "Failed ttgtgtgtgto load data. Please try again.")
                        else -> { }
                    }
                }
            }

            initViewModel()
            binding.category.selectButton(binding.btnAll)
            observeButtonsGroup()
            observeFloatingActionButton()
            changeFabColors()
            setFabAnimation()

            viewModel.collectSearch()

            binding.searchBarText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    viewModel.emitSearch(s.toString().lowercase())
                }
            })
        }
        else {
            binding.searchBar.visibility = View.INVISIBLE
            binding.filterBtn.visibility = View.INVISIBLE
            binding.category.visibility = View.INVISIBLE
            binding.rvProducts.visibility = View.INVISIBLE
            binding.fabCategory.visibility = View.INVISIBLE
            binding.progressBar3.visibility = View.INVISIBLE
            binding.emptyView.visibility = View.VISIBLE
        }
    }

    private fun hideAllFabButtons() {
        binding.apply {
            fabAccessories.visibility = View.GONE
            fabAccessories.startAnimation(fabClose)
            fabShirt.visibility = View.GONE
            fabShirt.startAnimation(fabClose)
            fabShoes.visibility = View.GONE
            fabShoes.startAnimation(fabClose)
            fabCategory.startAnimation(fabAntiClock)
        }
    }

    private fun showAllFabButtons() {
        binding.apply {
            fabAccessories.visibility = View.VISIBLE
            fabAccessories.startAnimation(fabOpen)
            fabShirt.visibility = View.VISIBLE
            fabShirt.startAnimation(fabOpen)
            fabShoes.visibility = View.VISIBLE
            fabShoes.startAnimation(fabOpen)
            fabCategory.startAnimation(fabClock)
        }
    }

    private fun decreaseBrightness() {
        val animator = ObjectAnimator.ofFloat(
            binding.overlayView,
            "alpha",
            0.0f,
            0.8f
        )
        animator.setDuration(300) // Animation duration in milliseconds (e.g., 2 seconds)
        animator.interpolator = AccelerateDecelerateInterpolator()
        binding.overlayView.setVisibility(View.VISIBLE)
        animator.start()
    }

    private fun increaseBrightness() {
        val animator = ObjectAnimator.ofFloat(
            binding.overlayView,
            "alpha",
            0.8f,
            0.0f
        )
        animator.setDuration(300) // Animation duration in milliseconds (e.g., 2 seconds)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun showFilterMenu(context: Context) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)
        val fromInputLayout = dialogView.findViewById<TextInputLayout>(R.id.from)
        val toInputLayout = dialogView.findViewById<TextInputLayout>(R.id.to)
        val fromInput = fromInputLayout.editText
        val toInput = toInputLayout.editText

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_filter))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                val fromValue = fromInput?.text?.toString()?.toFloatOrNull()
                val toValue = toInput?.text?.toString()?.toFloatOrNull()
                handleFilterSelection(fromValue, toValue)
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                // Handle cancel
            }
            .show()
    }

    private fun setUpRV(){
        val onClick: (isFav: Boolean, product: Product) -> Unit = { isFav, product ->
            if (CustomerData.getInstance(requireContext()).isLogged){
                if (isFav) {
                    favViewModel.insertFavProduct(product)
                } else {
                    product.id?.let { it1 -> favViewModel.deleteFavProduct(it1) }
                }
            }
            // if you need to make an action here handle it
        }
        val onCardClick: (id: Long) -> Unit = {
                val navController = NavHostFragment.findNavController(this)
                val action = CategoryFragmentDirections.actionToProductInfoFragment(it)
                navController.navigate(action)

        }
        val onStart: (id: Long, onTrue: ()->Unit, onFalse: ()->Unit) ->Unit = { id, onTrue, onFalse ->
            if (CustomerData.getInstance(requireContext()).isLogged){
                favViewModel.isFavProduct(id, onTrue, onFalse)
            }

        }

        adapter = ProductAdapter(requireContext(), onStart, onClick, onCardClick)

        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter

    }
    private fun updateCurrencyRates(newRate: Double) {
        adapter.updateCurrencyConversionRate(newRate)
    }
    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = CategoryFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)
        if (CustomerData.getInstance(requireContext()).isLogged){
            val favFactory = FavViewModelFactory(repository, CustomerData.getInstance(requireContext()).favListId)
            favViewModel = ViewModelProvider(this, favFactory).get(FavViewModel::class.java)
        }
       // if you need to make an action here handle it

        viewModel.getProducts()
        viewModel.getLatestRates()
        lifecycleScope.launch {
            viewModel.products.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> response.data.products?.let { showSuccess(it) }
                    is NetworkState.Failure -> showError(requireContext().getString(R.string.network_error), requireContext().getString(R.string.failed_load_data))
                    else -> { }
                }
            }
        }
    }



    private fun showLoading() {
        binding.progressBar3.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.INVISIBLE

    }

    private fun showSuccess(products: List<Product>) {
        binding.progressBar3.visibility = View.INVISIBLE
        binding.rvProducts.visibility = View.VISIBLE
        adapter.submitList(products)
    }

    private fun showError(title: String, message: String) {
        binding.progressBar3.visibility = View.INVISIBLE
        binding.rvProducts.visibility = View.INVISIBLE
        showAlertDialog(title, message)
    }


    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(requireContext().getString(R.string.sure)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun observeButtonsGroup() {
        binding.category.setOnSelectListener { button: ThemedButton ->
            when (button.id) {
                R.id.btn_women -> {
                    Log.d("setOnSelectListener", "btn_women")
                    mainCategory = WOMEN
                    viewModel.filterProducts(mainCategory, subCategory)
                }
                R.id.btn_kid -> {
                    Log.d("setOnSelectListener", "btn_kid")
                    mainCategory = KID
                    viewModel.filterProducts(mainCategory, subCategory)
                }
                R.id.btn_men -> {
                    Log.d("setOnSelectListener", "btn_men")
                    mainCategory = MEN
                    viewModel.filterProducts(mainCategory, subCategory)
                }
                R.id.btn_sale -> {
                    Log.d("setOnSelectListener", "btn_sale")
                    mainCategory = SALE
                    viewModel.filterProducts(mainCategory, subCategory)
                }
                R.id.btn_all -> {
                    Log.d("setOnSelectListener", "btn_all")
                    mainCategory = ALL
                    viewModel.filterProducts(mainCategory, subCategory)
                }
            }
        }
    }
    private fun observeFloatingActionButton() {
        binding.fabShoes.setOnClickListener {
            Log.d("setOnSelectListener", "fabShoes")
            subCategory = SHOES
            viewModel.filterProducts(mainCategory, subCategory)
            changeFabColors()
            changeSelectedFabColor()
        }

        binding.fabShirt.setOnClickListener {
            Log.d("setOnSelectListener", "fabShirt")
            subCategory = T_SHIRTS
            viewModel.filterProducts(mainCategory, subCategory)
            changeFabColors()
            changeSelectedFabColor()

        }

        binding.fabAccessories.setOnClickListener {
            Log.d("setOnSelectListener", "fabAccessories")
            subCategory = ACCESSORIES
            viewModel.filterProducts(mainCategory, subCategory)
            changeFabColors()
            changeSelectedFabColor()

        }
    }


    private fun changeFabColors() {
        binding.apply {
            fabAccessories.backgroundTintList = resources.getColorStateList(R.color.white)
            fabShirt.backgroundTintList = resources.getColorStateList(R.color.white)
            fabShoes.backgroundTintList = resources.getColorStateList(R.color.white)
        }
    }

    private fun changeSelectedFabColor() {
        binding.apply {
            when (subCategory) {
                ACCESSORIES -> {
                    fabAccessories.backgroundTintList =
                        resources.getColorStateList(R.color.light)
                }
                T_SHIRTS -> {
                    fabShirt.backgroundTintList = resources.getColorStateList(R.color.light)
                }
                SHOES -> {
                    fabShoes.backgroundTintList = resources.getColorStateList(R.color.light)
                }
            }
        }
    }

    companion object {
        const val WOMEN = "WOMEN"
        const val KID = "KID"
        const val MEN = "MEN"
        const val SALE = "SALE"
        const val ALL = " "
        const val SHOES = "SHOES"
        const val T_SHIRTS = "T-SHIRTS"
        const val ACCESSORIES = "ACCESSORIES"
    }

    override fun getValueconvertCurrency()  : Double{

        allCategoryFactory =
            CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCategoryViewModel = ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)
        var d = 0.0

        allCategoryViewModel.getLatestRates()
        lifecycleScope.launch {
            allCategoryViewModel.productCurrency.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> {
                        d= response.data.rates.EGP
                        Log.i("initViewModel", "initViewModel:${  response.data} ")
                        val exchangeRate = response.data.rates?.EGP ?: 1.0 // Default to 1.0 if rate is not available
                        updateCurrencyRates(exchangeRate)


                    }
                    is NetworkState.Failure -> showError(requireContext().getString(R.string.network_error), requireContext().getString(R.string.failed_load_data))
                    else -> { }
                }
            }
        }
        Log.i("d", "${d}: ")

        return   d
    }

    private fun handleFilterSelection(fromValue: Float?, toValue: Float?) {
        viewModel.filterProductsByPrice(fromValue, toValue)
    }

    private fun setFabAnimation() {
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_rotate_clock)
        fabAntiClock = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_rotate_anticlock)
    }


}