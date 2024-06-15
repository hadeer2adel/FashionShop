package com.example.fashionshop.Modules.Category.view

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentCategoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    var isAllFabsVisible: Boolean? = null
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: CategoryViewModel
    private var mainCategory = ""
    private var subCategory = ""
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
        initViewModel()
        binding.category.selectButton(binding.btnAll)
        observeButtonsGroup()
        observeFloatingActionButton()
        changeFabColors()


    }

    private fun hideAllFabButtons() {
        binding.apply {
            fabAccessories.visibility = View.GONE
            fabShirt.visibility = View.GONE
            fabShoes.visibility = View.GONE
        }
    }

    private fun showAllFabButtons() {
        binding.apply {
            fabAccessories.visibility = View.VISIBLE
            fabShirt.visibility = View.VISIBLE
            fabShoes.visibility = View.VISIBLE
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

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_filter))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                // Handle OK
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                // Handle cancel
            }
            .show()
    }

    private fun setUpRV(){
        val onClick: (product: Product) -> Unit = {
            val networkManager: NetworkManager = NetworkManagerImp.getInstance()
            val repository: Repository = RepositoryImp(networkManager)
            val factory = FavViewModelFactory(repository, CustomerData.getInstance(requireContext()).favListId)
            val favViewModel = ViewModelProvider(this, factory).get(FavViewModel::class.java)

            favViewModel.insertFavProduct(it)
        }
        val onCardClick: (id: Long) -> Unit = {
            val navController = NavHostFragment.findNavController(this)
            val action = CategoryFragmentDirections.actionToProductInfoFragment(it)
            navController.navigate(action)
        }
        adapter = ProductAdapter(requireContext(), false, onClick, onCardClick)
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = CategoryFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)
        viewModel.getProducts()
        viewModel.getLatestRates()
        lifecycleScope.launch {
            viewModel.products.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> response.data.products?.let { showSuccess(it) }
                    is NetworkState.Failure -> showError("Network Error", "Failed to load data. Please try again.")
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
            setPositiveButton("OK") { dialog, _ ->
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
                        resources.getColorStateList(R.color.gray1)
                }
                T_SHIRTS -> {
                    fabShirt.backgroundTintList = resources.getColorStateList(R.color.gray1)
                }
                SHOES -> {
                    fabShoes.backgroundTintList = resources.getColorStateList(R.color.gray1)
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



}