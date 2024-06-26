package com.example.fashionshop.Modules.OrderInfo.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionshop.Adapters.SingleOrderAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.LineItem
import com.example.fashionshop.Model.LineItemBody
import com.example.fashionshop.Model.Order
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.Home.viewModel.OrderInfoFactory
import com.example.fashionshop.Modules.Home.viewModel.OrderInfoViewModel
import com.example.fashionshop.Modules.Orders.viewModel.OrdersViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentOrderInfoBinding
import com.example.fashionshop.databinding.FragmentOrdersBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class OrderInfoFragment : Fragment() {
    // Binding instance
    private var _binding: FragmentOrderInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel:OrderInfoViewModel
    private val args:OrderInfoFragmentArgs by navArgs()
    private lateinit var adapter: SingleOrderAdapter
    private var currencyConversionRate: Double = 1.0
    private lateinit var allCategoryFactory: CategoryFactory
    private lateinit var allCategoryViewModel: CategoryViewModel
    override fun onStart() {
        super.onStart()
        initViewModelCurrency()
    }

    override fun onResume() {
        super.onResume()
        initViewModelCurrency()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModelCurrency()
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Set up the toolbar
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        setUpRV()
        initViewModelCurrency()
        initViewModel()
        viewModel.getOrder(args.orderId)
        // setUpRV()
    }

    private fun initViewModel() {
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = OrderInfoFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(OrderInfoViewModel::class.java)
        viewModel.getOrder(args.orderId)

        lifecycleScope.launch {
            viewModel.order.collectLatest { response ->
                when (response) {
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> showOrderDetails(response.data.order)
                    is NetworkState.Failure -> showError("Network Error", "Failed to load order. Please try again.")

                }
            }
        }

    }
    private fun initViewModelCurrency() {
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

    }
    private fun setUpRV(){
        adapter = SingleOrderAdapter(requireContext())
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter

    }

    private fun convertToLineItemBody(lineItem: LineItemBody): LineItemBody {
        return LineItemBody(
            id = lineItem.id,
            title = lineItem.title,
            quantity = lineItem.quantity,
            price = lineItem.price,
            sku = lineItem.sku,
            variant_id = lineItem.variant_id,
            properties= lineItem.properties


        )
    }

    private fun showOrderDetails(order: Order?) {
        binding.progressBar6.visibility = View.INVISIBLE
        binding.cardInfo.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.VISIBLE
        binding.textView44.visibility = View.VISIBLE

        order?.let {
            binding.tvOrderEmail.text = it.email
            binding.tvOrderPhome.text = it.billing_address?.phone
            binding.tvOrderAddress.text = it.billing_address?.address1
            val customer = CustomerData.getInstance(requireContext())
            val priceString = it.current_total_price
            val priceDouble = priceString?.toDoubleOrNull() ?: 0.0 // Convert to Double or default to 0.0 if conversion fails
            if (customer.currency == "USD") {
                binding.tvOrderPrice.text = convertCurrency(priceDouble)
            } else {
                binding.tvOrderPrice.text = String.format("%.2f", priceDouble) // Format directly if not converting
            }
            binding.currency.text = CustomerData.getInstance(requireContext()).currency
            val lineItemBodies = it.line_items?.map { item -> convertToLineItemBody(item) }?.toMutableList()
            adapter.submitList(lineItemBodies)
            Log.i("OrderInfo", "Order ID: ${it.id}")
        } ?: showError("Order Not Found", "The order details could not be found.")
    }
    private fun convertCurrency(amount: Double?): String {
        amount ?: return "" // Handle null or undefined amount gracefully
        val convertedPrice = amount / currencyConversionRate
        return String.format("%.2f", convertedPrice)
    }
    private fun showLoading() {
        binding.progressBar6.visibility = View.VISIBLE
        binding.cardInfo.visibility = View.INVISIBLE
        binding.rvProducts.visibility = View.INVISIBLE
        binding.textView44.visibility = View.INVISIBLE
    }

    private fun showError(title: String, message: String) {
        binding.progressBar6.visibility = View.INVISIBLE
        binding.cardInfo.visibility = View.INVISIBLE
        binding.rvProducts.visibility = View.INVISIBLE
        binding.textView44.visibility = View.INVISIBLE
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

}