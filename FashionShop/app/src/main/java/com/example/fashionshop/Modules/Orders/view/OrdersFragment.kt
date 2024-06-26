package com.example.fashionshop.Modules.Orders.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Adapters.OrdersAdapter
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Order
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Modules.Category.viewModel.CategoryFactory
import com.example.fashionshop.Modules.Category.viewModel.CategoryViewModel
import com.example.fashionshop.Modules.Home.view.HomeFragmentDirections
import com.example.fashionshop.Modules.Orders.view.OrdersFragmentDirections
import com.example.fashionshop.Modules.Orders.viewModel.OrdersFactory
import com.example.fashionshop.Modules.Orders.viewModel.OrdersViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.isNetworkConnected
import com.example.fashionshop.databinding.FragmentOrdersBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var adapter: OrdersAdapter
    private lateinit var viewModel: OrdersViewModel
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkConnected(requireContext())) {
            navController = NavHostFragment.findNavController(this)
            appBarConfiguration = AppBarConfiguration(navController.graph)

            // Set up the toolbar
            val toolbar = binding.toolbar
            NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

            setUpRV()
            initViewModel()
        }
        else
        {
            binding.rvOrders.visibility = View.INVISIBLE
            binding.toolbar.visibility = View.INVISIBLE
            binding.connectionLayout.visibility = View.VISIBLE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        if (!isNetworkConnected(requireContext())) {
            binding.rvOrders.visibility = View.INVISIBLE
            binding.toolbar.visibility = View.INVISIBLE
            binding.connectionLayout.visibility = View.VISIBLE
        }
    }

    private fun setUpRV(){
        val onCardClick: (order : Order) -> Unit = {
            Snackbar.make(binding.root,"Clicked on ${it.order_number}", Snackbar.LENGTH_SHORT).show()
            val action = it.id?.let { it1 ->
                OrdersFragmentDirections.actionOrderFragmentToOrderInfoFragment(
                    it1
                )
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }
        adapter = OrdersAdapter(requireContext(), onCardClick)
        binding.rvOrders.layoutManager =  LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
    }

    private fun showLoading() {
        binding.progressBar5.visibility = View.VISIBLE
        binding.rvOrders.visibility = View.INVISIBLE
        binding.emptyView.visibility = View.INVISIBLE
    }

    private fun showSuccess(orders: List<Order>) {
        binding.progressBar5.visibility = View.INVISIBLE
        if (orders.isEmpty()) {
            binding.rvOrders.visibility = View.INVISIBLE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.rvOrders.visibility = View.VISIBLE
            binding.emptyView.visibility = View.INVISIBLE
            adapter.submitList(orders)
        }
    }

    private fun showError(title: String, message: String) {
        binding.progressBar5.visibility = View.INVISIBLE
        binding.rvOrders.visibility = View.INVISIBLE
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

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = OrdersFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(OrdersViewModel::class.java)
        val id = CustomerData.getInstance(requireContext()).id
        viewModel.getOrders(id)
        lifecycleScope.launch {
            viewModel.orders.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success ->{
                        response.data.orders?.let { showSuccess(it) }
                        isLoading = false
                    }
                    is NetworkState.Failure -> {
                        isLoading = false
                    }
                }
            }
        }
    }




}
