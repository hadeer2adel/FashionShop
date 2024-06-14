package com.example.fashionshop.Modules.FavProductList.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionshop.Adapters.FavProductAdapter
import com.example.fashionshop.Adapters.ProductAdapter
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModel
import com.example.fashionshop.Modules.FavProductList.viewModel.FavViewModelFactory
import com.example.fashionshop.Modules.Products.view.ProductsFragmentDirections
import com.example.fashionshop.Modules.Products.viewModel.ProductsFactory
import com.example.fashionshop.Modules.Products.viewModel.ProductsViewModel
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.showDialog
import com.example.fashionshop.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log


class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: FavProductAdapter
    private lateinit var viewModel: FavViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycleView()
        initViewModel()

        viewModel.getFavProducts()
    }

    private fun setUpRecycleView(){
        val onClick: (id: Long) -> Unit = {
            val onAllow: () -> Unit = {
                viewModel.deleteFavProduct(it)
            }
            showDialog(
                requireContext(),
                R.string.delete_title,
                R.string.delete_body,
                onAllow
            )
        }
        val onCardClick: (id: Long) -> Unit = {
            val navController = NavHostFragment.findNavController(this)
//            val action = FavoriteFragmentDirections.actionToProductInfoFragment(it)
//            navController.navigate(action)
        }

        adapter = FavProductAdapter(requireContext(), true, onClick, onCardClick)
        adapter.submitList(emptyList())
        binding.recycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleView.adapter = adapter
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = FavViewModelFactory(repository, CustomerData.getInstance(requireContext()).favListId)
        viewModel = ViewModelProvider(this, factory).get(FavViewModel::class.java)

        lifecycleScope.launch {
            viewModel.product.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recycleView.visibility = View.GONE
                    }
                    is NetworkState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recycleView.visibility = View.VISIBLE
                        adapter.submitList(response.data.draft_order.line_items.drop(1))
                    }
                    is NetworkState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), response.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}