package com.example.fashionshop.Modules.Products.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionshop.Adapters.BrandAdapter
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.SmartCollection
import com.example.fashionshop.Modules.FavProductList.view.FavoriteFragmentDirections
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModelFactory
import com.example.fashionshop.Modules.Home.viewModel.HomeFactory
import com.example.fashionshop.Modules.Home.viewModel.HomeViewModel
import com.example.fashionshop.Modules.Products.viewModel.ProductsFactory
import com.example.fashionshop.Modules.Products.viewModel.ProductsViewModel
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.FragmentProductsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProductsFragment : Fragment()  {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private val args: ProductsFragmentArgs by navArgs()
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val view = binding.root
        //Toast.makeText(requireContext(), "Clicked on ${args.brandName}", Toast.LENGTH_SHORT).show()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRV()
        initViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            val action = ProductsFragmentDirections.actionToProductInfoFragment(it)
            navController.navigate(action)
        }
        adapter = ProductAdapter(requireContext(), false, onClick, onCardClick)
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = ProductsFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProductsViewModel::class.java)
        viewModel.getProducts(args.brandName)
        lifecycleScope.launch {
            viewModel.product.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> showLoading()
                    is NetworkState.Success -> response.data.products?.let { showSuccess(it) }
                    is NetworkState.Failure -> showError("Network Error", "Failed to load data. Please try again.")
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar2.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.INVISIBLE
    }

    private fun showSuccess(products: List<Product>) {
        binding.progressBar2.visibility = View.INVISIBLE
        binding.rvProducts.visibility = View.VISIBLE
        adapter.submitList(products)
    }

    private fun showError(title: String, message: String) {
        binding.progressBar2.visibility = View.INVISIBLE
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


}