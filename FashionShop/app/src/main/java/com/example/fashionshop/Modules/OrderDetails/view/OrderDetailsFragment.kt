package com.example.fashionshop.Modules.OrderDetails.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsFactory
import com.example.fashionshop.Modules.OrderDetails.viewModel.OrderDetailsViewModel
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartFactory
import com.example.fashionshop.Modules.ShoppingCard.viewModel.CartViewModel
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentOrderDetailsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderDetailsFragment() : Fragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var allProductFactory: CartFactory
    private lateinit var allProductViewModel: CartViewModel
    lateinit var allCodesFactory: OrderDetailsFactory
    private lateinit var allCodesViewModel: OrderDetailsViewModel
    val titlesList = mutableListOf<String>()
    private var currencyConversionRate: Double = 1.0
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customer = CustomerData.getInstance(requireContext())
        binding.currency.text = customer.currency
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        allCodesFactory = OrderDetailsFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCodesViewModel = ViewModelProvider(this, allCodesFactory).get(OrderDetailsViewModel::class.java)
        allCodesViewModel.getAdsCode()
        fetchTitlesList()

        allProductFactory = CartFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()), CustomerData.getInstance(requireContext()).cartListId)
        allProductViewModel = ViewModelProvider(this, allProductFactory).get(CartViewModel::class.java)

        allCategoryFactory = CategoryFactory(RepositoryImp.getInstance(NetworkManagerImp.getInstance()))
        allCategoryViewModel = ViewModelProvider(this, allCategoryFactory).get(CategoryViewModel::class.java)
        allCategoryViewModel.getLatestRates()
        lifecycleScope.launch {
            allCategoryViewModel.productCurrency.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> "showLoading()"
                    is NetworkState.Success -> {
                        Log.i("initViewModel", "initViewModel:${response.data}")
                        currencyConversionRate = response.data.rates?.EGP ?: 1.0
                    }
                    is NetworkState.Failure -> ""
                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            allProductViewModel.productCard.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val subtotal = response.data.draft_order.line_items.drop(1).sumByDouble { it.price?.toDoubleOrNull() ?: 0.0 }
                        val customer = CustomerData.getInstance(requireContext())
                        if (customer.currency == "USD") {
                            binding.subTotalValue.text = "${String.format("%.2f", convertCurrency(subtotal))}"
                        } else {
                            binding.subTotalValue.text = "${String.format("%.2f", subtotal)}"
                        }
                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.validate.setOnClickListener {
            validateCoupon()
        }
    }
    private fun convertCurrency(amount: Double?): Double {
        amount ?: return 0.0 // Handle null or undefined amount gracefully
        return amount / currencyConversionRate
    }
//    private fun convertCurrency(amount: Double?): String {
//        amount ?: return "" // Handle null or undefined amount gracefully
//        val convertedPrice = amount * currencyConversionRate
//        return String.format("%.2f", convertedPrice)
//    }

    private fun fetchTitlesList() {
        lifecycleScope.launch {
            allCodesViewModel.productCode.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> {}
                    is NetworkState.Success -> {
                        val value = response.data.price_rules
                        titlesList.clear()
                        titlesList.addAll(value.map { it.title })
                        binding.validate.isEnabled = true // Enable validate button
                    }
                    is NetworkState.Failure -> {
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateCoupon() {
        allCodesViewModel.getAdsCode()
        Log.i("getAdsCode", "titlesList: $titlesList")
        val couponCode = binding.coupon.text.toString()

        if (couponCode in titlesList) {
            val coupon = couponCode
            Toast.makeText(requireContext(), "Coupon Applied Successfully", Toast.LENGTH_LONG).show()
            lifecycleScope.launch {
                allCodesViewModel.productCode.collectLatest { response ->
                    when (response) {
                        is NetworkState.Loading -> {}
                        is NetworkState.Success -> {
                            val value = response.data
                            for (rule in value.price_rules) {
                                if (rule.title == coupon) {
                                    val valueOfDis = rule.value.toDoubleOrNull() ?: 0.0
                                    val subtotal = binding.subTotalValue.text.toString().toDoubleOrNull() ?: 0.0
                                    val discountAmount = subtotal * (valueOfDis / 100)
                                    val total = subtotal + discountAmount
                                    binding.discountValue.text = "${String.format("%.2f", kotlin.math.abs(valueOfDis))}%"
                                    binding.totalValue.text = String.format("%.2f", total)
                                    break
                                }
                            }
                        }
                        is NetworkState.Failure -> {
                            Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            showInvalidCouponDialog()
        }
    }

    private fun showInvalidCouponDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle("Invalid Coupon")
            setMessage("The coupon you entered is invalid.")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                binding.discountValue.text = "0"
                binding.totalValue.text = "0"
            }
        }
        alertDialogBuilder.create().show()
        Toast.makeText(requireContext(), "Coupon Code is Invalid", Toast.LENGTH_LONG).show()
        Log.i("Coupon", "False: ")
    }
}
