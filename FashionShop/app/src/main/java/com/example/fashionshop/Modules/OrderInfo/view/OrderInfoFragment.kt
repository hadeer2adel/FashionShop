package com.example.fashionshop.Modules.OrderInfo.view

import android.os.Bundle
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
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.LineItem
import com.example.fashionshop.Model.LineItemBody
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
        navController = NavHostFragment.findNavController(this)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Set up the toolbar
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        initViewModel()
        setUpRV()
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
                    is NetworkState.Loading -> {

                    }

                    is NetworkState.Success -> {
                        response.data.order.let { order ->
                            if (order != null) {
                                binding.tvOrderEmail.text = order.email
                                binding.tvOrderPhome.text = order.billing_address?.phone
                                binding.tvOrderAddress.text = order.billing_address?.address1
                                binding.tvOrderPrice.text = order.total_price
                                // Convert and submit the list
                                val lineItemBodies = order.line_items?.map { convertToLineItemBody(it) }?.toMutableList()
                                adapter.submitList(lineItemBodies)

                            }

                        }

                    }

                    is NetworkState.Failure -> {

                    }
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
            // Map properties from lineItem to LineItemBody
            // Assuming similar properties in both classes for this example
            id = lineItem.id,
            title = lineItem.title,
            quantity = lineItem.quantity,
            price = lineItem.price,
            sku = lineItem.sku,
            variant_id = lineItem.variant_id

        )
    }
}